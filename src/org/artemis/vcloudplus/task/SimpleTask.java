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

import org.artemis.vcloudplus.run.Task;
import org.artemis.vcloudplus.run.TaskException;
import org.artemis.vcloudplus.run.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SimpleTask simple task should extend this class.
 * SimpleTask.java is written at Apr 14, 2014
 * @author junli
 */
public abstract class SimpleTask extends Task {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private TaskStatus mTaskStatus = null;
	
	// subclass will run task in this function
	public abstract void runTask() throws TaskException;;
	
	/*
	 * (non-Javadoc)
	 * @see org.artemis.vcloudplus.run.Task#setStatus(org.artemis.vcloudplus.run.TaskStatus)
	 */
	public void setStatus(TaskStatus iTaskStatusKeeper)
	{
		mTaskStatus = iTaskStatusKeeper;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			this.runTask();
		} catch (TaskException e) {
			log.error(this.getClass().getName() + ": " + e.toString());
		} catch (Exception e) {
			log.error(this.getClass().getName() + ": " + e.getLocalizedMessage());
		} finally {
			if (mTaskStatus != null) {
				mTaskStatus.update(this);
			}
		}
	}

}
