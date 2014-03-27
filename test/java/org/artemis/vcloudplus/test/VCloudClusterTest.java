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
package org.artemis.vcloudplus.test;

import org.artemis.vcloudplus.common.BasicScheduleListener;
import org.artemis.vcloudplus.task.DummyJob;
import org.artemis.vcloudplus.task.Updatelease;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 * VCloudClusterTest TODO
 * VCloudClusterTest.java is written at Mar 24, 2014
 * @author junli
 */
@RunWith(JUnit4.class)
public class VCloudClusterTest {

	private static String sLog4J = "log4j.configuration";
	private static String sQuartz = "org.quartz.properties";
	
	@Test
	public void SingleNode()
	{
		String lLog4JConfig = System.getProperty(VCloudClusterTest.sLog4J);
		if (lLog4JConfig == null)
		{
			System.setProperty(VCloudClusterTest.sLog4J, 
					"file:" + System.getProperty("user.dir") + "/config/log4j.properties");
		}
		String lQuartzConfig = System.getProperty(VCloudClusterTest.sQuartz);
		if (lQuartzConfig == null)
		{
			System.setProperty(VCloudClusterTest.sQuartz, 
					System.getProperty("user.dir") + "/config/quartz_node_1.properties");
		}
		
		try {
			SchedulerFactory sf = new StdSchedulerFactory();
	        Scheduler lStdScheduler = sf.getScheduler();
		
			JobDetail lTestingJob = JobBuilder.newJob(DummyJob.class)
				.withIdentity("job1", "jobgrp1")
				.requestRecovery()
				.build();
			
			JobDetail lTestingJob2 = JobBuilder.newJob(DummyJob.class)
					.withIdentity("job2", "jobgrp1")
					.usingJobData(Updatelease.sUpdatedTimes, 0)
					.requestRecovery()
					.build();
		
			SimpleTrigger lTestingTrigger = TriggerBuilder.newTrigger()
				.withIdentity("trigger1", "triggergrp1")
				.startNow()
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInSeconds(2)
						.repeatForever())
				.build();
		
			SimpleTrigger lTestingTrigger2 = TriggerBuilder.newTrigger()
					.withIdentity("trigger2", "triggergrp1")
					.withSchedule(SimpleScheduleBuilder.simpleSchedule()
							.withIntervalInSeconds(7)
							.repeatForever())
					.build();
			
			JobKey lCheckJobKey = new JobKey("job1", "jobgrp1");
			if (!lStdScheduler.checkExists(lCheckJobKey))
				lStdScheduler.scheduleJob(lTestingJob, lTestingTrigger);
			
			lCheckJobKey = new JobKey("job2", "jobgrp1");
			if (!lStdScheduler.checkExists(lCheckJobKey))
				lStdScheduler.scheduleJob(lTestingJob2, lTestingTrigger2);
			
			lStdScheduler.getListenerManager()
				.addSchedulerListener(new BasicScheduleListener(lStdScheduler));
			
			lStdScheduler.start();
		
			try {
				synchronized (lStdScheduler) {
					lStdScheduler.wait();
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("going to shutdown");
			
			lStdScheduler.shutdown();
		
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("unkown exception");
		}
		
		System.out.println("down");
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		
	}

}
