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

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.artemis.vcloudplus.common.VCloud;

import com.vmware.vcloud.api.rest.schema.LeaseSettingsSectionType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.Vdc;

/**
 * VCloudTest TODO
 * VCloudTest.java is written at Sep 29, 2013
 * @author junli
 */
public class VCloudTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			String lVCloudURL = "https://tech-cloud.microstrategy.com";
			String lUserName = "junli@technology";
			String lUserPwd = "Return_jun%";
			
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
			HashMap<String, ReferenceType>  lVAppRefs = lVdc.getVappRefsByName();
			if (lVAppRefs.isEmpty()) {
				System.out.println("there is no vApp in current DC: " + lDCName);
				return;
			}
			System.out.println("VApps:");
			String lVAppName = "";
			for (String vAppName : lVAppRefs.keySet()) {
				System.out.println("	" + vAppName);
				// i only have one vApp
				lVAppName = vAppName;
			}
			
			ReferenceType lReferenceType = lVAppRefs.get(lVAppName);
			Vapp lVapp = VCloud.getVApp(lReferenceType);
			LeaseSettingsSectionType lLeaseSettingsSectionType = lVapp.getLeaseSettingsSection();
			
			XMLGregorianCalendar lXMLGregorianCalendar = lLeaseSettingsSectionType.getStorageLeaseExpiration();
			if (lXMLGregorianCalendar == null) {
				
				lLeaseSettingsSectionType.setDeploymentLeaseInSeconds(60 * 60 * 24 * 7);
				lLeaseSettingsSectionType.setStorageLeaseInSeconds(60 * 60 * 24 * 30);
				
			} else {
				// for storage 30 days
				Duration lStorageDuration = DatatypeFactory.newInstance().newDuration(true, 0, 0, 30, 0, 0, 0);
				lXMLGregorianCalendar.add(lStorageDuration);
				lLeaseSettingsSectionType.setStorageLeaseExpiration(lXMLGregorianCalendar);
				
				// for deployment 7 days
				Duration lDeploymentDuration = DatatypeFactory.newInstance().newDuration(true, 0, 0, 7, 0, 0, 0);
				XMLGregorianCalendar lDeloymentXMLGregorianCalendar = lLeaseSettingsSectionType.getDeploymentLeaseExpiration();
				lXMLGregorianCalendar.add(lDeploymentDuration);
				lLeaseSettingsSectionType.setStorageLeaseExpiration(lDeloymentXMLGregorianCalendar);
			}
			
			// create update task
			Task lUpdateTask = lVapp.updateSection(lLeaseSettingsSectionType);
			long lTaskTimeout = 1000 * 60 * 30;
			try {
				lUpdateTask.waitForTask(lTaskTimeout);
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("update lease is failed!");
			}
			
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VCloudException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				VCloud.VCloudLogout();
			} catch (VCloudException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
