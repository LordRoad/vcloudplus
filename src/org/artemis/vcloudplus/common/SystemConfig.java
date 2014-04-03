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
	public static String sVCloudPlusEmailAccount = "email";
	
	/**
	 * SMTP Server info
	 */
	public static String sVCloudPlusSMTPServer = "org.vcloudplus.vcloud.smpt";
	public static String sVCloudPlusSMTPServerHost = "host";
	public static String sVCloudPlusSMTPServerPort = "port";
	public static String sVCloudPlusSMTPServerAccount = "account";
	public static String sVCloudPlusSMTPServerPassWord = "password";
	
	/**
	 * Job configure
	 */
	public static String sVCloudPlusJobGroup = "org.vcloudplus.vcloud.job";
	public static String sVCloudPlusJobUpdateIntervalInHours = "updateIntervalInHours";
	public static String sVCloudPlusJobMonitorIntervalInMinutes = "monitorIntervalInMinutes";
	
	/**
	 * VCloud Runner
	 */
	public static String sLog4J = "log4j.configuration";
	public static String sDefaultLog4JPath = 
			"file:" + System.getProperty("user.dir") + "/config/log4j.properties";
	
	public static String sVCloudPlus = "vcloudplus.properties";
	public static String sDefaultVCloudPlusPath = 
			System.getProperty("user.dir") + "/config/vcloudplus.properties";
	
	public static String sQuartz = "org.quartz.properties";
	
	// backup quartz property path
	public static String sQuartzBak = "org.quartz.properties.bak";
	
	public static String sDefaultQuartzPath = 
			System.getProperty("user.dir") + "/config/quartz_node.properties";
	
	public static String sDefaultVCloudJobGroup = "VCloudPlus";
	public static String sDefaultVCloudTriggerGroup = "VCloudPlus";
	
	public static String sJobExt = "_job";
	public static String sTriggerExt = "_trigger";
	
	public static String sEmailAccount = "email";
	
}
