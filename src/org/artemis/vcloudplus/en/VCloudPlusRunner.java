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
package org.artemis.vcloudplus.en;

import java.util.List;

import org.artemis.vcloudplus.common.BasicScheduleListener;
import org.artemis.vcloudplus.common.SystemConfig;
import org.artemis.vcloudplus.run.ObservableSet;
import org.artemis.vcloudplus.run.Observer;
import org.artemis.vcloudplus.task.ClusterMgt;
import org.artemis.vcloudplus.task.Updatelease;
import org.artemis.vcloudplus.task.UpdateleaseListener;
import org.artemis.vcloudplus.util.ConfigureReader;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.JobPersistenceException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;
import org.quartz.utils.PropertiesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * VCloudPlusRunner vcloudplus runner
 * VCloudPlusRunner.java is written at Mar 25, 2014
 * @author junli
 */
@SuppressWarnings("deprecation")
public final class VCloudPlusRunner implements Observer, Runnable  {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private Scheduler mScheduler = null;
	private VCloudPlusError mPersistenceError = VCloudPlusError.NONE;
	private VCloudPlusRunningMode mVCloudPlusRunningMode = VCloudPlusRunningMode.Cluster;
	private boolean mDebug = false;
	
	/**
	 * VCloudPlusStatus:
	 * NONE, normal ended, always exception is thrown
	 * NeedInCluster, cluster environment is detected, need add current node into cluster
	 * NeedInMemory, cluster environment is unreachable (PersistenceException)
	 * 				need run current node in memory 
	 */
	public enum VCloudPlusError {
		NONE,
		NeedInCluster,
		NeedInMemory
	}
	
	public enum VCloudPlusRunningMode {
		Cluster,
		Memory,
		StandAlone
	}
	
	public VCloudPlusRunner() {
		VCloudPlusEventsCenter.Instance().addObserver(VCloudPlusEvents.VCloudPlusDBIsOnLine, this);
	}
	
	public VCloudPlusError HasPersistenceError() {
		return mPersistenceError;
	}
	
	protected void EnableDebug() {
		mDebug = true;
	}
	
	protected void DisableDebug() {
		mDebug = false;
	}
	
	protected void SetRunningMode(VCloudPlusRunningMode iVCloudPlusRunningMode) {
		mVCloudPlusRunningMode = iVCloudPlusRunningMode;
	}
	
	@Override
	public void run() {
		if (mDebug) {
			log.info("runner is in debug mode");
		}
		
		try {
			SchedulerFactory sf = new StdSchedulerFactory();
			mScheduler = sf.getScheduler();
			
	        ConfigureReader lConfigureReader = new ConfigureReader();
	        PropertiesParser lPropertiesParser = lConfigureReader.GetGroup(System.getProperty(SystemConfig.sVCloudPlus), 
	        		SystemConfig.sVCloudPlusJobGroup);
	        
	        int lUpdateIntervalInHours = SystemConfig.sDefaultVCloudPlusJobUpdateIntervalInHours;
	        int lMonitorInervalInMinutes = SystemConfig.sDefaultVCloudPlusJobMonitorIntervalInMinutes;
	        if (lPropertiesParser != null) {
	        	try {
	        		lUpdateIntervalInHours = lPropertiesParser.getIntProperty(SystemConfig.sVCloudPlusJobUpdateIntervalInHours);
	        	} catch (NumberFormatException e) {
	        		log.warn("failed to get " + SystemConfig.sVCloudPlusJobUpdateIntervalInHours 
							+ e.getLocalizedMessage() + "\nuse default value: " + Integer.toString(lUpdateIntervalInHours));
	        	}
	        	try {
	        		lMonitorInervalInMinutes = lPropertiesParser.getIntProperty(SystemConfig.sVCloudPlusJobMonitorIntervalInMinutes);
	        	} catch (NumberFormatException e) {
	        		log.warn("failed to get " + SystemConfig.sVCloudPlusJobMonitorIntervalInMinutes 
							+ e.getLocalizedMessage() + "\nuse default value: " + Integer.toString(lMonitorInervalInMinutes));
	        	}
	        }
	        
	        /**
	         * 1. update lease job
	         */
	        JobKey lCheckJobKey;
	        if (!mDebug) {
				JobDetail lUpdateleaseJob = JobBuilder.newJob(Updatelease.class)
					.withIdentity(Updatelease.class.getName() + SystemConfig.sJobExt, SystemConfig.sDefaultVCloudJobGroup)
					.requestRecovery(true)
					.build();
			
				lUpdateleaseJob.getJobDataMap().put(Updatelease.sVCloudPlusConfigPath, 
						System.getProperty(SystemConfig.sVCloudPlus));
				
				// update info
				lUpdateleaseJob.getJobDataMap().put(Updatelease.sVCloudPlusUpdateInfo, "record some update info");
				
				SimpleTrigger lUpdateleaseTrigger = TriggerBuilder.newTrigger()
					.withIdentity(Updatelease.class.getName() + SystemConfig.sTriggerExt, SystemConfig.sDefaultVCloudTriggerGroup)
					.startNow()
					.withSchedule(SimpleScheduleBuilder.simpleSchedule()
							.withIntervalInHours(lUpdateIntervalInHours)
							.withMisfireHandlingInstructionFireNow()
							.repeatForever())
					.build();
				
				lCheckJobKey = new JobKey(Updatelease.class.getName() + SystemConfig.sJobExt, SystemConfig.sDefaultVCloudJobGroup);
				if (!mScheduler.checkExists(lCheckJobKey))
					mScheduler.scheduleJob(lUpdateleaseJob, lUpdateleaseTrigger);
				
				mScheduler.getListenerManager()
					.addJobListener(new UpdateleaseListener(), KeyMatcher.keyEquals(lCheckJobKey));
	        }
			
			/**
			 * 2. monitor db job
			 */
			JobDetail lMonitorDBJob = JobBuilder.newJob(ClusterMgt.class)
					.withIdentity(ClusterMgt.class.getName() + SystemConfig.sJobExt, SystemConfig.sDefaultVCloudJobGroup)
					.requestRecovery(true)
					.build();
			
			SimpleTrigger lMonitorDBTrigger = TriggerBuilder.newTrigger()
					.withIdentity(ClusterMgt.class.getName() + SystemConfig.sTriggerExt, SystemConfig.sDefaultVCloudTriggerGroup)
					.withSchedule(SimpleScheduleBuilder.simpleSchedule()
							.withIntervalInMinutes(lMonitorInervalInMinutes)
							.withMisfireHandlingInstructionFireNow()
							.repeatForever())
					.startNow()
					.build();
			
			lCheckJobKey = new JobKey(ClusterMgt.class.getName() + SystemConfig.sJobExt, SystemConfig.sDefaultVCloudJobGroup);
			if (!mScheduler.checkExists(lCheckJobKey))
				mScheduler.scheduleJob(lMonitorDBJob, lMonitorDBTrigger);
			
			/**
			 * 3. need adding job listener
			 */
			mScheduler.getListenerManager()
				.addSchedulerListener(new BasicScheduleListener(mScheduler));
			
			mScheduler.start();
		
			try {
				synchronized (mScheduler) {
					mScheduler.wait();
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			log.info("going to shutdown");
			mScheduler.shutdown();
		
		} catch (JobPersistenceException e) {
			// persistence exception, run it as in memory mode
			log.error(e.getLocalizedMessage());
			mPersistenceError = VCloudPlusError.NeedInMemory;
			
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error(e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getLocalizedMessage());
		}
		
		VCloudPlusEventsCenter.Instance().deleteObserver(VCloudPlusEvents.VCloudPlusDBIsOnLine, this);
		log.info("vcloudplus runner is shut down");
	}
	
	/**
	 * check if all other running jobs are done execution except caller
	 * if caller is a running job
	 * @return
	 */
	private boolean isAllRunningJobsDoneExceptCaller() {
		List<JobExecutionContext> lRunningJobs;
		try {
			lRunningJobs = mScheduler.getCurrentlyExecutingJobs();
		} catch (SchedulerException e) {
			// ignore
			return true;
		}
		int lRunningJobsCount = lRunningJobs.size(); 
		if (lRunningJobsCount <= 1) { // ignore caller
			return true;
		}
		
		int iter = 0;
		for (iter = 0; iter < lRunningJobs.size(); ++iter) {
			if (lRunningJobs.get(iter).getJobRunTime() == -1) { // not completed
				break;
			}
		}
		if (iter >= lRunningJobs.size()) {
			return true;
		}		
		return false;
	}
	
	/**
	 * wait until all running jobs are done or timeout
	 * @param iTimeout, milliseconds
	 * 			< 0, wait forever
	 */
	private void wait4jobs(long iTimeout) {
		boolean lAllDone = false;
		boolean lbForever = iTimeout < 0 ? true : false;
		long lInterval = 512;
		long lLoops = iTimeout / lInterval;
		long lRemain = iTimeout % lInterval;

		while (lbForever || lLoops > 0) {
			if (isAllRunningJobsDoneExceptCaller()) {
				return;
			}
			try {
				Thread.sleep(lInterval);
			} catch (InterruptedException e) {
				// ignore
			}
			--lLoops;
		}

		if (!lAllDone && lRemain > 0 && !isAllRunningJobsDoneExceptCaller()) {
			try {
				Thread.sleep(lRemain);
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}
	
	@Override
	public void update(ObservableSet o, Object arg) {
		if (arg instanceof Boolean) {
			/**
			 * if true, database is online, we need to change running mode if current mode is in-memory
			 */
			if (((Boolean)arg).booleanValue() == true && 
					mVCloudPlusRunningMode != VCloudPlusRunningMode.Cluster) {
				mVCloudPlusRunningMode = VCloudPlusRunningMode.Cluster;
				mPersistenceError = VCloudPlusError.NeedInCluster;
				
				// wait 30s for running jobs
				wait4jobs(30 * 1000);
				
				// stop running current scheduler
				synchronized (mScheduler) {
					mScheduler.notifyAll();
				}
				log.info("change running mode of vcloudplus server from in-memory to cluster!");
			} else {
				/**
				 * database is offline, we need to run in memory mode
				 */
				if (((Boolean)arg).booleanValue() == false &&
						mVCloudPlusRunningMode == VCloudPlusRunningMode.Cluster) {
					mVCloudPlusRunningMode = VCloudPlusRunningMode.Memory;
					mPersistenceError = VCloudPlusError.NeedInMemory;
					
					// stop running current scheduler
					synchronized (mScheduler) {
						mScheduler.notifyAll();
					}
					log.info("change running mode of vcloudplus server from cluster to in-memory!");
				}
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String lLog4JConfig = System.getProperty(SystemConfig.sLog4J);
		if (lLog4JConfig == null)
		{
			System.setProperty(SystemConfig.sLog4J, 
					SystemConfig.sDefaultLog4JPath);
		}
		
		VCloudPlusRunningMode lVCloudPlusRunningMode = VCloudPlusRunningMode.Cluster;
		
		String lQuartzConfig;
		if (lVCloudPlusRunningMode == VCloudPlusRunningMode.Cluster) {
			lQuartzConfig = System.getProperty(SystemConfig.sQuartz);
			if (lQuartzConfig == null)
			{
				System.setProperty(SystemConfig.sQuartz, 
						SystemConfig.sDefaultQuartzPath);
			}
		} else {
			lQuartzConfig = System.getProperty(SystemConfig.sQuartz);
			if (lQuartzConfig != null) {
				System.setProperty(SystemConfig.sQuartzBak, lQuartzConfig);
				System.clearProperty(SystemConfig.sQuartz);
			}
		}
		
		String lVCloudPlusConfig = System.getProperty(SystemConfig.sVCloudPlus);
		if (lVCloudPlusConfig == null)
		{
			System.setProperty(SystemConfig.sVCloudPlus, 
					SystemConfig.sDefaultVCloudPlusPath);
		}
		
		final Logger log = LoggerFactory.getLogger("main");
		log.info("vcloud plus manager is going to run");
		
		try {
			boolean lRunning = true;
			while (lRunning) {
				VCloudPlusRunner lVCloudPlusRunner = new VCloudPlusRunner();
				lVCloudPlusRunner.SetRunningMode(lVCloudPlusRunningMode);
				//lVCloudPlusRunner.EnableDebug();
				
				Thread lVCloudPlusThread = new Thread(lVCloudPlusRunner);
				lVCloudPlusThread.run();
				while (lVCloudPlusThread.isAlive())
				{
					try {
						lVCloudPlusThread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}	
				
				switch (lVCloudPlusRunner.HasPersistenceError()) {
				case NONE:
				{
					lRunning = false;
					break;
				}
				case NeedInCluster:
				{
					lQuartzConfig = System.getProperty(SystemConfig.sQuartz);
					if (lQuartzConfig == null)
					{
						lQuartzConfig = System.getProperty(SystemConfig.sQuartzBak);
						if (lQuartzConfig != null) {
							System.setProperty(SystemConfig.sQuartz, lQuartzConfig);
						} else {
							System.setProperty(SystemConfig.sQuartz, SystemConfig.sDefaultQuartzPath);
						}
					}
					lVCloudPlusRunningMode = VCloudPlusRunningMode.Cluster;
					break;
				}
				case NeedInMemory:
				{
					System.clearProperty(SystemConfig.sQuartz);
					lVCloudPlusRunningMode = VCloudPlusRunningMode.Memory;
					break;
				}
				}
				
			}
			log.info("vcloud plus manager is shut down");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getLocalizedMessage());
		}
	}
	
}
