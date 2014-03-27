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
 * SystemConfig TODO
 * SystemConfig.java is written at Mar 26, 2014
 * @author junli
 */
public class SystemConfig {

	/**
	 * DB parameters
	 * 	org.quartz.dataSource.myDS.driver: com.mysql.jdbc.Driver
		org.quartz.dataSource.myDS.URL: jdbc:mysql://localhost:3306/quartz
		org.quartz.dataSource.myDS.user: root
		org.quartz.dataSource.myDS.password:
		org.quartz.dataSource.myDS.maxConnections: 5
		org.quartz.dataSource.myDS.validationQuery: select 0
	 */
	public static String sDataSourceConfig = "org.quartz.dataSource";
	public static String sDataSource = "org.quartz.jobStore.dataSource";
	public static String sDataSourceDriver = "driver";
	public static String sDataSourceURL = "URL";
	public static String sDataSourceUser = "user";
	public static String sDataSourcePasswd = "password";
	public static String sDataSourceValidationQuery = "validationQuery";
	
	
	
	
}
