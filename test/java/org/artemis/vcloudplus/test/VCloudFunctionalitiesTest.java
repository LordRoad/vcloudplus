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

import org.artemis.vcloudplus.task.Updatelease;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

/**
 * VCloudFunctionalitiesTest TODO
 * VCloudFunctionalitiesTest.java is written at Dec 5, 2013
 * @author junli
 */
public class VCloudFunctionalitiesTest extends TestCase {
	private static Logger sClassLog = LoggerFactory.getLogger(VCloudFunctionalitiesTest.class);
	
	private SchedulerFactory mSchedulerFactory = new StdSchedulerFactory();
	
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() {
		
	}
	
	public void ResetLease() throws SchedulerException, InterruptedException {
		Scheduler lsched = mSchedulerFactory.getScheduler();
		final int lIntervalMinutes = 2;
		final int lRepeatCount = 3;
		
		// update lease job
		JobDetail lUpdatelease = JobBuilder.newJob(Updatelease.class)
				.withIdentity("UpdateLease", "VCloudplusTest")
				.build();
		lUpdatelease.getJobDataMap().put(Updatelease.sUpdatedTimes, 0);
		
		// trigger with firing misfired jobs 
		Date lStartTime = DateBuilder.nextGivenMinuteDate(null, lIntervalMinutes);
		SimpleTrigger lUpdateleaseTrigger = (SimpleTrigger) TriggerBuilder.newTrigger()
				.withIdentity("UpdateLease", "VCloudplusTest")
				.startAt(lStartTime)
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInMinutes(lIntervalMinutes)
						.withRepeatCount(lRepeatCount)
						.withMisfireHandlingInstructionFireNow())
				.build()
				;
		
		Date lRun = lsched.scheduleJob(lUpdatelease, lUpdateleaseTrigger);
		sClassLog.info(lUpdatelease.getKey() + 
				" will run at" +
				lRun + ", add repeat " +
				lUpdateleaseTrigger.getRepeatCount() + " times at every " +
				lUpdateleaseTrigger.getRepeatInterval() / 1000 + " seconds.");
		
		lsched.start();
		
		while (lUpdateleaseTrigger.getTimesTriggered() < lRepeatCount) {
			Thread.sleep(60*1000);
		}
		
		lsched.shutdown();
		
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() {
		try {
			mSchedulerFactory.getScheduler().clear();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
}
