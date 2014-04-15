/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.artemis.vcloudplus.run;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SimpleJobScheduler ScheduledThreadPoolExecutor wrapper 
 * SimpleJobScheduler.java is written at Apr 14, 2014
 * @author junli
 */
public class SimpleJobScheduler implements InnerScheduler, TaskStatus {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	// based on ThreadPoolExecutor
	private ScheduledThreadPoolExecutor mScheduledThreadPoolExecutor;
	private Map<String, TaskInfo> mTaskRepeatCountMap = new ConcurrentHashMap<String, TaskInfo>();
	
	private static long sSingleWaitingMilliseconds = 500;
	public static int sSchedulerCorePoolSize = 4;
	
	public SimpleJobScheduler() {
		mScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(sSchedulerCorePoolSize);
	}
	
	/**
	 * schedule a task
	 * @param iTask
	 * @throws 	Exception
	 * 			RejectedExecutionException - if the task cannot be scheduled for execution
	 *			NullPointerException - if command is null
	 *			IllegalArgumentException - if period less than or equal to zero
	 */
	public void schedule(Task iTask) throws Exception {
		long lTaskInterval = iTask.getInterval();
		if (lTaskInterval < 1) {
			throw new Exception("task interval should be a positive");
		}
		long lTaskRepeatCount = iTask.getRepeatCount();
		
		Calendar lCalendar = Calendar.getInstance();
		long lCurrentTime = lCalendar.getTimeInMillis();
		lCalendar.setTime(iTask.getStartTime() == null ? new Date() : iTask.getStartTime());
		
		long lSchedulerTime = lCalendar.getTimeInMillis();
		long lDelayTime = lSchedulerTime > lCurrentTime ? lSchedulerTime - lCurrentTime : 0;
		
		if (mTaskRepeatCountMap.containsKey(iTask.getTaskName())) {
			throw new Exception("task name (" + iTask.getTaskName() + ") is existed, please set a new name.");
		}
		try {
			iTask.setStatus(this);
			
			ScheduledFuture<?> lScheduledFuture = 
					mScheduledThreadPoolExecutor.scheduleAtFixedRate(iTask, lDelayTime, lTaskInterval * 1000, TimeUnit.MILLISECONDS);
			
			mTaskRepeatCountMap.put(iTask.getTaskName(), new TaskInfo(lTaskRepeatCount, lScheduledFuture));
		} finally {
			// 
		}
	}
	
	/**
	 * since using ScheduledThreadPoolExecutor as core scheduler, shutdown means no new tasks
	 * but here shutdown means stop the running scheduler
	 */
	public void shutdown() {
		List<Runnable> lRemainTasks = mScheduledThreadPoolExecutor.shutdownNow();
		try {
			mScheduledThreadPoolExecutor.awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// ignore
		}

//		if (lRemainTasks.size() > 0) {
//			log.warn("remaining jobs: ");
//			for (Runnable lRunnable : lRemainTasks) {
//				try {
//					// Waits if necessary for the computation to complete, and then retrieves its result
//					// so it's not necessary
//					log.warn(((ScheduledFuture<Task>)lRunnable).get().getTaskProperties().toString());
//				} catch (InterruptedException e) {
//					// ignore
//				} catch (ExecutionException e) {
//					// ignore
//				}
//			}
//		}
		log.info("simple job scheduer is shutdown");
	}
	
	protected class TaskInfo {
		public long mRepeatCount;
		public ScheduledFuture<?> mScheduledFuture;
		
		public TaskInfo(long iRepeatCount, ScheduledFuture<?> iScheduledFuture) {
			mRepeatCount = iRepeatCount;
			mScheduledFuture = iScheduledFuture;
		}
	}
	
	@Override
	public void cancel(String iTaskName) throws Exception {
		TaskInfo lTaskInfo = mTaskRepeatCountMap.get(iTaskName);
		if (lTaskInfo == null) return;
		
		lTaskInfo.mScheduledFuture.cancel(true); // interrupt running job 
		mTaskRepeatCountMap.remove(lTaskInfo);
		
	}

	@Override
	public boolean done(Task iTask) {
		TaskInfo lTaskInfo = mTaskRepeatCountMap.get(iTask.getTaskName());
		
		return lTaskInfo == null ? true : 
			lTaskInfo.mRepeatCount < 0 ? false : true;
	}

	@Override
	public void update(Task iTask) {
		TaskInfo lTaskInfo = mTaskRepeatCountMap.get(iTask.getTaskName());
		if (lTaskInfo == null || lTaskInfo.mRepeatCount < 0) return;
		
		if (lTaskInfo.mRepeatCount - 1 <= 0) {
			//  in-progress tasks are allowed to complete
			lTaskInfo.mScheduledFuture.cancel(false);
			mTaskRepeatCountMap.remove(iTask.getTaskName());
		} else {
			--lTaskInfo.mRepeatCount;
		}
	}

	@Override
	public void waitDone(long iWaitMillisecond) throws TimeoutException {
		long lSingleWaitMillisec = iWaitMillisecond >= 0 ? 
				(sSingleWaitingMilliseconds < iWaitMillisecond ? sSingleWaitingMilliseconds : iWaitMillisecond) :
					sSingleWaitingMilliseconds;
		
		long lWaitLoops = iWaitMillisecond < 0 ? 0 : iWaitMillisecond / lSingleWaitMillisec;
		long lRemain = iWaitMillisecond % lSingleWaitMillisec;
		boolean lRunForever = iWaitMillisecond < 0 ? true : false;
		
		while (lRunForever || lWaitLoops > 0) {
			//if (mScheduledThreadPoolExecutor.getQueue().size() > 0) {
			if (mTaskRepeatCountMap.size() > 0) {
				try {
					Thread.sleep(lSingleWaitMillisec);
				} catch (InterruptedException e) {
					// ignore
				}
				--lWaitLoops;
			} else {
				return;
			}
		}
		if (lRemain > 0) {
			try {
				Thread.sleep(lSingleWaitMillisec);
			} catch (InterruptedException e) {
				// ignore
			}
		}
		//if (mScheduledThreadPoolExecutor.getQueue().size() > 0) {
		if (mTaskRepeatCountMap.size() > 0) {
			throw new TimeoutException();
		}
	}

}
