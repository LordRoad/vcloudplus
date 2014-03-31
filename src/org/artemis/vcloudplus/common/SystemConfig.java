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

/**
 * SystemConfig system configure for vcloud plus
 * SystemConfig.java is written at Mar 26, 2014
 * @author junli
 */
public class SystemConfig {

	/**
	 * default values
	 */
	public static int sDefaultVCloudPlusJobUpdateIntervalInHours = 120;
	public static int sDefaultVCloudPlusJobMonitorIntervalInMinutes = 60;
	public static int sDefaultVCloudPlusDeploymentLease = 7;
	public static int sDefaultVCloudPlusStorageLease = 30;
	
	/**
	 * VCloudPlus configure
	 */
	public static String sVCloudPlusGroup = "org.vcloudplus.vcloud";
	public static String sVCloudPlusURL = "url";
	public static String sVCloudPlusUser = "user";
	public static String sVCloudPlusPasswd = "password";
	public static String sVCloudPlusOrg = "org";
	public static String sVCloudPlusDC = "datacenter";
	public static String sVCloudPlusVApp = "vapp";
	public static String sVCloudPlusStorage = "storage";
	public static String sVCloudPlusDeployment = "deployment";
	
	/**
	 * Job configure
	 */
	public static String sVCloudPlusJobGroup = "org.vcloudplus.vcloud.job";
	public static String sVCloudPlusJobUpdateIntervalInHours = "updateIntervalInHours";
	public static String sVCloudPlusJobMonitorIntervalInMinutes = "monitorIntervalInHours";
	
	
	
}
