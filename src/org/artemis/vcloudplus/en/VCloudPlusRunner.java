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

import org.artemis.vcloudplus.common.BasicScheduleListener;
import org.artemis.vcloudplus.task.Updatelease;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobPersistenceException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * VCloudPlusRunner TODO
 * VCloudPlusRunner.java is written at Mar 25, 2014
 * @author junli
 */
public final class VCloudPlusRunner implements Runnable {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	public static String sLog4J = "log4j.configuration";
	public static String sQuartz = "org.quartz.properties";
	
	public static String sDefaultVCloudJobGroup = "VCloudPlus";
	private static String sDefaultVCloudTriggerGroup = "VCloudPlus";
	
	private VCloudPlusStatus mPersistenceError = VCloudPlusStatus.NONE;
	
	/**
	 * VCloudPlusStatus:
	 * NONE, normal ended, always exception is thrown
	 * NeedInCluster, cluster envrionment is detected, need add current node into cluster
	 * NeedInMemory, cluster envrionment is unreachable (PersistenceException)
	 * 				need run current node in memory 
	 */
	public enum VCloudPlusStatus {
		NONE,
		NeedInCluster,
		NeedInMemory
	}
	
	public VCloudPlusStatus HasPersistenceError() {
		return mPersistenceError;
	}
	
	@Override
	public void run() {
		try {
			SchedulerFactory sf = new StdSchedulerFactory();
	        Scheduler lStdScheduler = sf.getScheduler();
		
			JobDetail lTestingJob = JobBuilder.newJob(Updatelease.class)
				.withIdentity(Updatelease.class.getName() + "_job", sDefaultVCloudJobGroup)
				.requestRecovery()
				.build();
		
			SimpleTrigger lTestingTrigger = TriggerBuilder.newTrigger()
				.withIdentity(Updatelease.class.getName() + "_Trigger", sDefaultVCloudTriggerGroup)
				.startNow()
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInHours(24)
						.repeatForever())
				.build();
			
			JobKey lCheckJobKey = new JobKey(Updatelease.class.getName() + "_job", sDefaultVCloudJobGroup);
			if (!lStdScheduler.checkExists(lCheckJobKey))
				lStdScheduler.scheduleJob(lTestingJob, lTestingTrigger);
			
			lStdScheduler.getListenerManager()
				.addSchedulerListener(new BasicScheduleListener(lStdScheduler));
			
			lStdScheduler.start();
		
			try {
				synchronized (lStdScheduler) {
					lStdScheduler.wait();
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			log.info("going to shutdown");
			lStdScheduler.shutdown();
		
		} catch (JobPersistenceException e) {
			// persistence exception, run it as in memory mode
			log.error(e.getLocalizedMessage());
			mPersistenceError = VCloudPlusStatus.NeedInMemory;
			
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error(e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getLocalizedMessage());
		}
		
		log.info("vcloudplus runner is shut down");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String lLog4JConfig = System.getProperty(VCloudPlusRunner.sLog4J);
		if (lLog4JConfig == null)
		{
			System.setProperty(VCloudPlusRunner.sLog4J, 
					"file:" + System.getProperty("user.dir") + "/config/log4j.properties");
		}
		String lQuartzConfig = System.getProperty(VCloudPlusRunner.sQuartz);
		if (lQuartzConfig == null)
		{
			System.setProperty(VCloudPlusRunner.sQuartz, 
					System.getProperty("user.dir") + "/config/quartz_node.properties");
		}
		
		final Logger log = LoggerFactory.getLogger("main");
		log.info("vcloud plus manager is going to run");
		
		try {
			boolean lRunning = true;
			while (lRunning) {
				VCloudPlusRunner lVCloudPlusRunner = new VCloudPlusRunner();
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
					
					break;
				}
				case NeedInMemory:
				{
					System.clearProperty(VCloudPlusRunner.sQuartz);
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
