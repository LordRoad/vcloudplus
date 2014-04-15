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
import java.util.UUID;

/**
 * Task: the task design is referred to Roller inner task
 * Task.java is written at Sep 29, 2013
 * @author junli
 */
public abstract class Task implements Runnable {
	public String mTaskName = UUID.randomUUID().toString();
	
	public void init(String iTaskName) {
		mTaskName = iTaskName;
	}
	
	/**
	 * get task name, if not set, it will be random UUID.
	 * @return
	 */
	public final String getTaskName() {
		return mTaskName;
	}

	/**
	 * How many times task will be executed.		
	 * 
	 * @return < 0, means repeat forever, = 0 will be run once 
	 */
	public abstract long getRepeatCount();
	
	 /**
     * How often should the task run, in seconds.
     *
     * example: 60 means this task runs once every minute.
     *
     * @return The interval the task should be run at, in minutes.
     */
    public abstract long getInterval();
	
    /**
     * Get the time, in minutes, this task wants to be leased for.
     *
     * example: 5 means the task is allowed 5 minutes to run.
     *
     * @return The time this task should lease its lock for, in minutes.
     */
    public abstract int getLeaseTime();
    
    /**
     * set task status keeper to current task for updating scheduled task
     * @param iTaskStatus
     */
    public abstract void setStatus(TaskStatus iTaskStatusKeeper);
    
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
    public Properties getTaskProperties() {
    	Properties lProperties = new Properties();
    	lProperties.put("taskname", this.getClass().getName() + ":" + this.getTaskName());
    	lProperties.put("repeats", Long.toString(this.getRepeatCount()));
    	lProperties.put("interval (sec)", Long.toString(this.getInterval()));
    	lProperties.put("start", this.getStartTime());
    	
    	return lProperties;
    }
    
}
