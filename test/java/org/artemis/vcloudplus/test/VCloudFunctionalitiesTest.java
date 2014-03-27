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

import org.artemis.vcloudplus.common.BasicScheduleListener;
import org.artemis.vcloudplus.common.BasicTriggerListener;
import org.artemis.vcloudplus.en.VCloudPlusRunner;
import org.artemis.vcloudplus.task.Updatelease;
import org.artemis.vcloudplus.task.UpdateleaseListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * VCloudFunctionalitiesTest TODO
 * VCloudFunctionalitiesTest.java is written at Dec 5, 2013
 * @author junli
 */
@RunWith(JUnit4.class)
public class VCloudFunctionalitiesTest {
	private static Logger sClassLog = LoggerFactory.getLogger(VCloudFunctionalitiesTest.class);
	
	private SchedulerFactory mSchedulerFactory = new StdSchedulerFactory();
	
	@Test
	public void ResetLease() throws SchedulerException, InterruptedException {
		
		String lLog4JConfig = System.getProperty(VCloudPlusRunner.sLog4J);
		if (lLog4JConfig == null)
		{
			System.setProperty(VCloudPlusRunner.sLog4J, 
					"file:" + System.getProperty("user.dir") + "/config/log4j.properties");
		}
		
		Scheduler lsched = mSchedulerFactory.getScheduler();
		final int lIntervalMinutes = 2;
		final int lRepeatCount = 3;
		
		// update lease job
		JobDetail lUpdatelease = JobBuilder.newJob(Updatelease.class)
				.withIdentity("UpdateLease", "VCloudplusTest")
				
				.build();
		lUpdatelease.getJobDataMap().put(Updatelease.sUpdatedTimes, 0);
		
		/**
		 * 1. Create jobs and triggers
		 */
		
		// trigger with firing misfired jobs 
		Date lStartTime = DateBuilder.nextGivenSecondDate(null, lIntervalMinutes);
		// Date lStartTime = DateBuilder.nextGivenMinuteDate(null, lIntervalMinutes);
		SimpleTrigger lUpdateleaseTrigger = TriggerBuilder.newTrigger()
				.withIdentity("UpdateLease", "VCloudplusTest")
				.startAt(lStartTime)
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInSeconds(lIntervalMinutes)
						.withRepeatCount(lRepeatCount)
						.withMisfireHandlingInstructionFireNow()
						)
				.build()
				;
		
		
		CronTrigger lCronTrigger = TriggerBuilder.newTrigger()
				.withIdentity("UpdateLease2", "VCloudplusTest")
				.withSchedule(CronScheduleBuilder.cronSchedule("0 42 10 * * ?")
						.withMisfireHandlingInstructionFireAndProceed())
				.build();
		
		/**
		 * 2. add listeners
		 */
		
		lsched.getListenerManager()
			.addSchedulerListener(new BasicScheduleListener(lsched));
		
		lsched.getListenerManager()
			.addJobListener(new UpdateleaseListener(), 
					KeyMatcher.keyEquals(new JobKey("UpdateLease", "VCloudplusTest")));
		
		lsched.getListenerManager()
			.addTriggerListener(new BasicTriggerListener(), 
					KeyMatcher.keyEquals(new TriggerKey("UpdateLease", "VCloudplusTest")));
		
		/**
		 * 3. schedule jobs, triggers
		 */
		
		Date lRun = lsched.scheduleJob(lUpdatelease, lUpdateleaseTrigger);
		sClassLog.info(lUpdatelease.getKey() + 
				" will run at" +
				lRun + ", add repeat " +
				lUpdateleaseTrigger.getRepeatCount() + " times at every " +
				lUpdateleaseTrigger.getRepeatInterval() / 1000 + " seconds.");
		
		/**
		 * 4. run schedule
		 */
		
		lsched.start();
		
		while (lUpdateleaseTrigger.getTimesTriggered() < lRepeatCount) {
			Thread.sleep(60*1000);
		}
		
		lsched.shutdown();
		
	}
	
}
