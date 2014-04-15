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

import java.util.concurrent.TimeoutException;

/**
 * Scheduler TODO
 * Scheduler.java is written at Apr 14, 2014
 * @author junli
 */
public interface InnerScheduler {
	
	/**
	 * schedule one task, if it could be scheduled, some exception will be thrown
	 * @param iTask
	 * @throws Exception
	 */
	public void schedule(Task iTask) throws Exception;
	
	/**
	 * cancel one task if it exists
	 * @param iTaskName
	 * @throws Exception
	 */
	public void cancel(String iTaskName) throws Exception;

	/**
	 * shut down inner scheduler
	 */
	public void shutdown();
	
	/**
	 * wait all tasks are finished or time out
	 * @param iWaitMilliSecond, timeout millisecond
	 * 				< 0 means wait forever
	 * @throws TimeoutException
	 */
	public void waitDone(long iWaitMillisecond) throws TimeoutException;
	
}
