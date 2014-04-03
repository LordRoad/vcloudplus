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

import java.util.Properties;

import org.artemis.vcloudplus.common.SystemConfig;
import org.artemis.vcloudplus.util.ConfigureReader;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.utils.PropertiesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UpdateleaseListener TODO
 * UpdateleaseListener.java is written at Dec 5, 2013
 * @author junli
 */
public class UpdateleaseListener implements JobListener {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	/* (non-Javadoc)
	 * @see org.quartz.JobListener#getName()
	 */
	@Override
	public String getName() {
		return UpdateleaseListener.class.getName();
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobListener#jobExecutionVetoed(org.quartz.JobExecutionContext)
	 */
	@Override
	public void jobExecutionVetoed(JobExecutionContext arg0) {
		log.info(this.getName() + " is vetoed");
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobListener#jobToBeExecuted(org.quartz.JobExecutionContext)
	 */
	@Override
	public void jobToBeExecuted(JobExecutionContext arg0) {
		log.info(this.getName() + " is to be executed");
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobListener#jobWasExecuted(org.quartz.JobExecutionContext, org.quartz.JobExecutionException)
	 */
	@Override
	public void jobWasExecuted(JobExecutionContext arg0,
			JobExecutionException arg1) {
		log.info(this.getName() + " was executed");
		
		/**
		 * after execution, send information to user. i have to let it go now. there is no time for this
		 */
//		try {
//			String lUpdateInfo = (String)arg0.getJobDetail().getJobDataMap().get(Updatelease.sVCloudPlusUpdateInfo);
//			
//			ConfigureReader lConfigureReader = new ConfigureReader();
//			Properties lProperties = lConfigureReader.Read(System.getProperty(SystemConfig.sVCloudPlus));
//			
//			PropertiesParser lVCloudPlus = lConfigureReader.GetGroup(lProperties, SystemConfig.sVCloudPlusGroup);
//			String lEmail = lVCloudPlus.getStringProperty(SystemConfig.sVCloudPlusEmailAccount);
//			
//			PropertiesParser lSMTPServer = lConfigureReader.GetGroup(lProperties, SystemConfig.sVCloudPlusSMTPServer);
//			
//			String lSMTPServerHost = lVCloudPlus.getStringProperty(SystemConfig.sVCloudPlusSMTPServerHost);
//			int lSMTPServerPort = lVCloudPlus.getIntProperty(SystemConfig.sVCloudPlusSMTPServerPort);
//			String lSMTPServerAccount = lVCloudPlus.getStringProperty(SystemConfig.sVCloudPlusSMTPServerAccount);
//			String lSMTPServerPassWord = lVCloudPlus.getStringProperty(SystemConfig.sVCloudPlusSMTPServerPassWord);
//			
//			if (lEmail != null) {
//				Properties props = new Properties();
//			    props.put("mail.smtp.auth", "true");
//			    props.put("mail.smtp.starttls.enable", "true");
//			    props.put("mail.smtp.host", lSMTPServerHost);
//			    props.put("mail.smtp.port", lSMTPServerPort);
//				
//				
//			}
//		} catch (Exception e) {
//			log.error(e.getLocalizedMessage());
//		}
	}

}
