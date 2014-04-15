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
package org.artemis.vcloudplus.task;

import java.util.Date;

import org.artemis.vcloudplus.run.TaskException;

/**
 * DBMonitorTask TODO
 * DBMonitorTask.java is written at Apr 14, 2014
 * @author junli
 */
public class DBMonitorTask extends SimpleTask {

	private long mRepeatCount = -1;
	private long mInterval = 0;
	private int mLeaseTime = 0;
	private Date mStartTime = null;
	
	ClusterMgt mClusterMgt;
	
	public DBMonitorTask(long iRepeatCount, long iInterval, Date iStartTime, int iLeaseTime) {
		mRepeatCount = iRepeatCount;
		mInterval = iInterval;
		mStartTime = iStartTime;
		mLeaseTime = iLeaseTime;
	}
	
	@Override
	public long getRepeatCount() {
		return mRepeatCount;
	}

	@Override
	public long getInterval() {
		return mInterval;
	}

	@Override
	public int getLeaseTime() {
		return mLeaseTime;
	}

	@Override
	public Date getStartTime() {
		return mStartTime;
	}

	@Override
	public void runTask() throws TaskException {
		try {
			if (mClusterMgt == null)
				mClusterMgt = new ClusterMgt();
			
			// monitor job
			mClusterMgt.execute(null);
		} catch (Exception e) {
			throw new TaskException(e.getLocalizedMessage());
		}
		
	}
	
}
