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
package org.artemis.vcloudplus.test;

import java.util.HashMap;

import org.artemis.vcloudplus.common.VCloud;
import org.artemis.vcloudplus.handle.VAppHandle;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.Vdc;

/**
 * VAppHandleTest TODO
 * VAppHandleTest.java is written at Sep 29, 2013
 * @author junli
 */
public class VAppHandleTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String lVCloudURL = "https://tech-cloud.microstrategy.com";
			String lUserName = "junli@technology";
			String lUserPwd = "Return_jun^";
			
			HashMap<String, ReferenceType> lOrgMap = VCloud.VCloudLogin(lVCloudURL, lUserName, lUserPwd);
			if (lOrgMap.isEmpty()) {
				System.out.println("there is no organization in current vcloud: " + lVCloudURL);
				return;
			}
			
			String lOrgName = "Technology";
			System.out.println("Organizations:");
			for (String organizationName : lOrgMap.keySet()) {
				System.out.println("	" + organizationName);
				if (organizationName.contains("Tech")) {
					lOrgName = organizationName;
				}
			}
			
			String lDCName = "Technology";
			Vdc lVdc = VCloud.findVdc(lOrgName, lDCName);
			if (lVdc == null) {
				System.out.println("there is no data center in current organization: " + lOrgName);
				return;
			}
			
			VAppHandle lVAppHandle = new VAppHandle();
			lVAppHandle.ResetLease(lVdc, "vApp_junli_2k864");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				VCloud.VCloudLogout();
			} catch (VCloudException e) {
				e.printStackTrace();
			}
		}
		
	}

}
