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

import java.util.Date;
import java.util.Properties;

/**
 * Task: the task design is referred to Roller inner task
 * Task.java is written at Sep 29, 2013
 * @author junli
 */
public abstract class Task implements Runnable {
	public String mTaskName = "";
	
	public void init(String iTaskName) {
		mTaskName = iTaskName;
	}
	
	/**
	 * get task name
	 * @return
	 */
	public final String getTaskName() {
		return mTaskName;
	}

	 /**
     * How often should the task run, in minutes.
     *
     * example: 60 means this task runs once every hour.
     *
     * @return The interval the task should be run at, in minutes.
     */
    public abstract int getInterval();
	
    /**
     * Get the time, in minutes, this task wants to be leased for.
     *
     * example: 5 means the task is allowed 5 minutes to run.
     *
     * @return The time this task should lease its lock for, in minutes.
     */
    public abstract int getLeaseTime();
    
    /**
     * get begin of task
     * 
     * @return
     */
    public abstract Date getStartTime();
    
    /**
     * not supported now
     * @return
     */
    protected Properties getTaskProperties() {
    	return null;
    }
    
}
