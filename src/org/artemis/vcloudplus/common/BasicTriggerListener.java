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
package org.artemis.vcloudplus.common;

import org.quartz.listeners.TriggerListenerSupport;

/**
 * BasicTriggerListener basic trigger listener for debugging or logging
 * BasicTriggerListener.java is written at Mar 24, 2014
 * @author junli
 */
public class BasicTriggerListener extends TriggerListenerSupport {

	/* (non-Javadoc)
	 * @see org.quartz.TriggerListener#getName()
	 */
	@Override
	public String getName() {
		return BasicTriggerListener.class.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.quartz.listeners.TriggerListenerSupport#triggerFired(org.quartz.Trigger, org.quartz.JobExecutionContext)
	 */
	public void triggerFired(org.quartz.Trigger trigger, org.quartz.JobExecutionContext context)
	{
		getLog().info(this.getName() + " is fired");
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.quartz.listeners.TriggerListenerSupport#triggerMisfired(org.quartz.Trigger)
	 */
	public void triggerMisfired(org.quartz.Trigger trigger)
	{
		getLog().info(this.getName() + " is misfired");
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.quartz.listeners.TriggerListenerSupport#triggerComplete(org.quartz.Trigger, org.quartz.JobExecutionContext, org.quartz.Trigger.CompletedExecutionInstruction)
	 */
	public void triggerComplete(org.quartz.Trigger trigger, 
			org.quartz.JobExecutionContext context, 
			org.quartz.Trigger.CompletedExecutionInstruction triggerInstructionCode)
	{
		getLog().info(this.getName() + " is completed");
	}
	
}
