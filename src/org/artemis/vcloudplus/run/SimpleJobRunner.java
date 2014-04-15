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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * SimpleJobRunner Simple job runner, and it will accept interface of task
 * SimpleJobRunner.java is written at Apr 13, 2014
 * 
 * @see org.artemis.vcloudplus.run.Task
 * @author junli
 * @deprecated it has been replaced by <code>ScheduledThreadPoolExecutor<code>
 */
@Deprecated
public final class SimpleJobRunner {
	
	private BlockingQueue<Runnable> mLinkedBlockingQueue = new LinkedBlockingQueue<Runnable>();
	private ThreadPoolExecutor mDefaultThreadpool = new ThreadPoolExecutor(
			4, // core pool size														
			16, // max pool size
			3 * 60, // keep idle thread alive time
			TimeUnit.SECONDS, // time unit
			mLinkedBlockingQueue, // linked blocking queue
			new ThreadPoolExecutor.AbortPolicy() // throw exception when pool is full										
	);
	
	private static SimpleJobRunner sSimpleJobRunner = new SimpleJobRunner();
	private static Object sSynchronizeObj = new Object();
	
	SimpleJobRunner() {
		mDefaultThreadpool.allowCoreThreadTimeOut(true);
		
	}
	
	public static SimpleJobRunner instance() {
		if (sSimpleJobRunner == null) {
			synchronized (sSynchronizeObj) {
				sSimpleJobRunner = new SimpleJobRunner();
			}
		}
		return sSimpleJobRunner;
	}
	
	public static void close() {
		Thread.currentThread().checkAccess();
		if (sSimpleJobRunner != null) {
			synchronized (sSynchronizeObj) {
				sSimpleJobRunner = null;
			}
		}
	}
	
	public void shutdown() {
		mDefaultThreadpool.shutdown();
	}
	
	public void ScheduleJob(Task iTask) {
		mDefaultThreadpool.execute(iTask);
	}
	
}
