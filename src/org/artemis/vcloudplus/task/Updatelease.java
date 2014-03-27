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

import org.artemis.vcloudplus.common.VCloud;
import org.artemis.vcloudplus.handle.VAppHandle;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
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
	
	public static String sUpdatedTimes = "UpdatedTimes";
	
	/*
	 * (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext iJEC) throws JobExecutionException {
	//	try {
			
			System.out.println("execute job");
			return;
		/*	
			String lVCloudURL = "https://tech-cloud.microstrategy.com";
			log.trace("vcloud url: " + lVCloudURL);
			String lUserName = "junli@technology";
			log.trace("vcloud user: " + lUserName);
			String lUserPwd = "Return_jun!";

			HashMap<String, ReferenceType> lOrgMap = VCloud.VCloudLogin(
					lVCloudURL, lUserName, lUserPwd);
			if (lOrgMap.isEmpty()) {
				log.warn("there is no organization in current vcloud: "
								+ lVCloudURL);
				return;
			}

			String lOrgName = "Technology";
			log.info("Organizations: ");
			for (String organizationName : lOrgMap.keySet()) {
				log.info(organizationName + ", ");
				if (organizationName.contains("Tech")) {
					lOrgName = organizationName;
				}
			}

			String lDCName = "Technology";
			Vdc lVdc = VCloud.findVdc(lOrgName, lDCName);
			if (lVdc == null) {
				log.warn("there is no data center in current organization: "
								+ lOrgName);
				return;
			}

			VAppHandle lVAppHandle = new VAppHandle();
			if (lVAppHandle.ResetLease(lVdc, "vApp_junli_2k864")) {
				log.info("reset is done");
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			e.printStackTrace();
		} finally {
			try {
				VCloud.VCloudLogout();
			} catch (VCloudException e) {
				log.error(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
		*/
	}

}
