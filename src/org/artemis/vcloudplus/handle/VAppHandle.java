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
package org.artemis.vcloudplus.handle;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.artemis.vcloudplus.common.VCloud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vcloud.api.rest.schema.LeaseSettingsSectionType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.Vdc;

/**
 * VAppHandle TODO VAppHandle.java is written at Sep 29, 2013
 * 
 * @author junli
 */
public class VAppHandle extends VCloud {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	public boolean ResetLease(Vdc iVdc, String strVAppName) {
		try {
			HashMap<String, ReferenceType>  lVAppRefs = iVdc.getVappRefsByName();
			if (lVAppRefs.isEmpty()) {
				log.warn("there is no vApp in current DC");
				return false;
			}
			
			String lVApp = "VApps: ";
			for (String vAppName : lVAppRefs.keySet()) {
				lVApp += vAppName + ", ";
			}
			log.info(lVApp);
			
			ReferenceType lReferenceType = lVAppRefs.get(strVAppName);
			if (lReferenceType == null) {
				log.warn("there is no vApp in current DC which is named: " + strVAppName);
				return false;
			}
			
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
			
			lUpdateTask.waitForTask(lTaskTimeout);
			return true;
		} catch (TimeoutException e) {
			log.error(e.getLocalizedMessage());
		} catch (VCloudException e) {
			log.error(e.getLocalizedMessage());
		} catch (DatatypeConfigurationException e) {
			log.error(e.getLocalizedMessage());
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		} 
		
		log.warn("update lease is failed!");
		return false;
	}
	
}
