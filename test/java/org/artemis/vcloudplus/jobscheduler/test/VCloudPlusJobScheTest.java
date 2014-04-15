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
package org.artemis.vcloudplus.jobscheduler.test;

import java.util.Date;

import org.artemis.vcloudplus.run.SimpleJobScheduler;
import org.artemis.vcloudplus.run.TaskException;
import org.artemis.vcloudplus.task.SimpleTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * VCloudPlusJobScheTest TODO
 * VCloudPlusJobScheTest.java is written at Apr 15, 2014
 * @author junli
 */
@RunWith(JUnit4.class)
public class VCloudPlusJobScheTest extends SimpleTask {
	int mRunningTimes = 0;
	
	@Override
	public void runTask() throws TaskException {
		System.out.println(this.getClass().getName() + " runs " + Integer.toString(++mRunningTimes));
	}

	@Override
	public long getRepeatCount() {
		
		return -1;
	}

	@Override
	public long getInterval() {
		
		return 2;
	}

	@Override
	public int getLeaseTime() {
		
		return 0;
	}

	@Override
	public Date getStartTime() {
		
		return new Date();
	}
	
	/**
	 * @param args
	 */
	@Test
	public void SimpleJobSchedulerTest() {
		
		SimpleJobScheduler lSimpleJobScheduler = new SimpleJobScheduler();
		try {
			lSimpleJobScheduler.schedule(new VCloudPlusJobScheTest());
			
			lSimpleJobScheduler.waitDone(-1);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
	}

}
