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

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.logging.Level;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.constants.Version;
import com.vmware.vcloud.sdk.samples.FakeSSLSocketFactory;

/**
 * 
 * VCloud VCloud Basic class for vcloud operation, just copy it from sample code
 * VCloud.java is written at Dec 5, 2013
 * @author junli
 */
public class VCloud {
	protected static VcloudClient sVCloudClient = null;
	private static HashMap<String, ReferenceType> sOrgNameMap = null;

	/**
	 * login with cloud URL, user name and password
	 * 
	 * @param vCloudURL
	 * @param username
	 * @param password
	 * @return
	 * @throws KeyManagementException
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws VCloudException
	 */
	public static HashMap<String, ReferenceType> VCloudLogin(String vCloudURL,
			String username, String password) throws KeyManagementException,
			UnrecoverableKeyException, NoSuchAlgorithmException,
			KeyStoreException, VCloudException {

		if (sVCloudClient != null) {
			return sOrgNameMap;
		}
		VcloudClient.setLogLevel(Level.OFF);
		// VcloudClient.setLogLevel(Level.ALL);

		sVCloudClient = new VcloudClient(vCloudURL, Version.V5_1);
		sVCloudClient.registerScheme("https", 443,
				FakeSSLSocketFactory.getInstance());
		sVCloudClient.login(username, password);
		sOrgNameMap = sVCloudClient.getOrgRefsByName();
		if (!sOrgNameMap.isEmpty()) {

		} else {
			System.out.println("	Invalid login for user " + username);
			return null;
		}

		return sOrgNameMap;
	}

	public static void VCloudLogout() throws VCloudException {
		if (sVCloudClient != null) {
			sVCloudClient.logout();
		}
	}

	/**
	 * Data Center belongs to Organization which contains vApp, Media,
	 * vAppTemplate and NetWork
	 * 
	 * 
	 * To instantiate a vApp template and operate the resulting vApp, you need
	 * the object references (href values) for the catalog in which the vApp
	 * template will be entered and the vDC in which the vApp will be deployed.
	 * The Organization class implements several methods that return references
	 * to vDCs and catalogs. findVdc gets the href of the vDC named on the
	 * command line.
	 * 
	 * @param vdcName
	 * @param orgName
	 * @return {@link Vdc}
	 * @throws VCloudException
	 */
	public static Vdc findVdc(String orgName, String vdcName)
			throws VCloudException {
		ReferenceType orgRef = sVCloudClient.getOrgRefsByName().get(orgName);
		Organization org = Organization.getOrganizationByReference(
				sVCloudClient, orgRef);
		ReferenceType vdcRef = org.getVdcRefByName(vdcName);
		System.out.println("VDC - " + vdcRef.getName());
		return Vdc.getVdcByReference(sVCloudClient, vdcRef);
	}

	/**
	 * get vapp by reference
	 * 
	 * @param iReferenceType
	 * @return
	 * @throws Exception 
	 */
	public static Vapp getVApp(ReferenceType iReferenceType)
			throws Exception {
		try {
			Vapp lVapp = Vapp.getVappByReference(sVCloudClient, iReferenceType);
			return lVapp;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
