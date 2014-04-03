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

import java.util.Date;

import org.artemis.vcloudplus.en.VCloudPlusRunner;
import org.artemis.vcloudplus.task.ClusterMgt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 * VCloudPlusMonitorTest TODO
 * VCloudPlusMonitorTest.java is written at Apr 1, 2014
 * @author junli
 */
@RunWith (JUnit4.class)
public class VCloudPlusMonitorTest {
	
	private SchedulerFactory mSchedulerFactory = new StdSchedulerFactory();
	
	@Test
	public void TestMonitor() throws SchedulerException {
		
		String lLog4JConfig = System.getProperty(VCloudPlusRunner.sLog4J);
		if (lLog4JConfig == null)
		{
			System.setProperty(VCloudPlusRunner.sLog4J, 
					"file:" + System.getProperty("user.dir") + "/config/log4j.properties");
		}
		
		Scheduler lsched = mSchedulerFactory.getScheduler();
		final int lIntervalMinutes = 1;
		final int lRepeatCount = 1;
		
		/**
		 * 1. Create jobs and triggers
		 */
		
		// ClusterMgt job
		JobDetail lUpdatelease = JobBuilder.newJob(ClusterMgt.class)
				.withIdentity("ClusterMgt", "VCloudplusTest")
				
				.build();
		
		// trigger with firing misfired jobs 
		Date lStartTime = DateBuilder.nextGivenSecondDate(null, lIntervalMinutes);
		// Date lStartTime = DateBuilder.nextGivenMinuteDate(null, lIntervalMinutes);
		SimpleTrigger lUpdateleaseTrigger = TriggerBuilder.newTrigger()
				.withIdentity("ClusterMgt", "VCloudplusTest")
				.startNow()
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInMinutes(lIntervalMinutes)
						.withRepeatCount(lRepeatCount)
						.withMisfireHandlingInstructionFireNow()
						)
				.build()
				;
		
		/**
		 * 3. schedule jobs, triggers
		 */
		
		Date lRun = lsched.scheduleJob(lUpdatelease, lUpdateleaseTrigger);
		
		/**
		 * 4. run schedule
		 */
		
		lsched.start();
		
		while (lUpdateleaseTrigger.getTimesTriggered() < lRepeatCount) {
			try {
				Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		lsched.shutdown();
		
	}
	
}
