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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.artemis.vcloudplus.common.SystemConfig;
import org.artemis.vcloudplus.common.VCloud;
import org.artemis.vcloudplus.handle.VAppHandle;
import org.artemis.vcloudplus.util.ConfigureReader;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.utils.PropertiesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.Vdc;

/**
 * Updatelease update cloud lease and it's used to test quartz
 * Updatelease.java is written at Dec 3, 2013
 * @author junli
 */
public class Updatelease implements Job {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	public static String sVCloudPlusUpdateInfo = "VCloudPlusUpdateInfo";
	public static String sVCloudPlusConfigPath = "VCloudPlusConfigPath";
	public static String sDefaultVCloudPlusConfigPath = "./config/vcloudplus.properties";
	
	/*
	 * (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext iJEC) throws JobExecutionException {
		try {
			String lVCloudConfigurePath;
			JobDataMap lJobDataMap = iJEC.getJobDetail().getJobDataMap();
			if (lJobDataMap == null || !lJobDataMap.containsKey(sVCloudPlusConfigPath)) {
				lVCloudConfigurePath = sDefaultVCloudPlusConfigPath;
			} else {
				lVCloudConfigurePath = (String) lJobDataMap.get(sVCloudPlusConfigPath);
			}
			
			ConfigureReader lConfigureReader = new ConfigureReader();
			Properties lVCloudPlusProperties = lConfigureReader.Read(lVCloudConfigurePath);
			if (lVCloudPlusProperties == null) {
				return;
			}
			PropertiesParser lPropertiesParser = lConfigureReader.GetGroup(lVCloudPlusProperties, SystemConfig.sVCloudPlusGroup) ;
			
			String lVCloudURL = lPropertiesParser.getStringProperty(SystemConfig.sVCloudPlusURL);
			log.trace("vcloud url: " + lVCloudURL);
			String lUserName = lPropertiesParser.getStringProperty(SystemConfig.sVCloudPlusUser);
			log.trace("vcloud user: " + lUserName);
			String lUserPwd = lPropertiesParser.getStringProperty(SystemConfig.sVCloudPlusPasswd);

			/**
			 * 1. login to vcloud
			 */
			HashMap<String, ReferenceType> lOrgMap = VCloud.VCloudLogin(
					lVCloudURL, lUserName, lUserPwd);
			if (lOrgMap == null || lOrgMap.isEmpty()) {
				log.warn("there is no organization in current vcloud: "
								+ lVCloudURL);
				return;
			}

			/**
			 * 2. get organization set or specified organization 
			 */
			String lOrgName = lPropertiesParser.getStringProperty(SystemConfig.sVCloudPlusOrg);
			Set<String> lOrgSet = new HashSet<String>();
			String lOrgList = "organization: ";
			if (lOrgName != null) {
				lOrgSet.add(lOrgName);
				lOrgList += lOrgName;
			} else {
				for (String organizationName : lOrgMap.keySet()) {
					lOrgList += organizationName + ", ";
					lOrgSet.add(organizationName);
				}
			}
			log.info(lOrgList);
			
			String lDCName = lPropertiesParser.getStringProperty(SystemConfig.sVCloudPlusDC);
			if (lDCName == null) {
				log.error("data center name should be set!");
				return;
			}
			
			/**
			 * 3. update each vapp in each organization
			 */
			VAppHandle lVAppHandle = new VAppHandle();
			for (String organizationName : lOrgSet) {
				Vdc lVdc = null;
				try
				{
					lVdc = VCloud.findVdc(organizationName, lDCName);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				if (lVdc == null) {
					log.warn("there is no data center in current organization: "
									+ lOrgName);
					continue;
				}
				
				if (lVAppHandle.ResetLease(lVdc, lPropertiesParser)) {
					log.info("reset is done");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getLocalizedMessage());
		} finally {
			try {
				VCloud.VCloudLogout();
			} catch (VCloudException e) {
				log.error(e.getLocalizedMessage());
			}
		}
	}

}
