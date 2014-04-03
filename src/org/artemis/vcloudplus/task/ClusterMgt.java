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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import org.artemis.vcloudplus.common.BasicConnectionProvider;
import org.artemis.vcloudplus.common.SystemConfig;
import org.artemis.vcloudplus.en.VCloudPlusEvents;
import org.artemis.vcloudplus.en.VCloudPlusEventsCenter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.utils.DBConnectionManager;
import org.quartz.utils.JNDIConnectionProvider;
import org.quartz.utils.PoolingConnectionProvider;
import org.quartz.utils.PropertiesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClusterMgt this job is used to check if cluster is on. if true, stop current
 * in memory scheduler and run as a node 
 * 
 * ClusterMgt.java is written at Mar 25, 2014
 * 
 * @author junli
 */
public class ClusterMgt implements Job {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private boolean initDataSources(Properties iProps) {

		PropertiesParser cfg = new PropertiesParser(iProps);
		String[] dsNames = cfg
				.getPropertyGroups(StdSchedulerFactory.PROP_DATASOURCE_PREFIX);

		DBConnectionManager dbMgr = null;
		for (int i = 0; i < dsNames.length; i++) {
			PropertiesParser pp = new PropertiesParser(cfg.getPropertyGroup(
					StdSchedulerFactory.PROP_DATASOURCE_PREFIX + "."
							+ dsNames[i], true));
			
			/**
			 * it's not be used for now
			 */
			//String cpClass = pp.getStringProperty(
			//		StdSchedulerFactory.PROP_CONNECTION_PROVIDER_CLASS, null);

			// custom connectionProvider... not supported
			//if (cpClass != null) {
				// ConnectionProvider cp = null;
				// try {
				// cp = (ConnectionProvider)
				// loadHelper.loadClass(cpClass).newInstance();
				// } catch (Exception e) {
				// initException = new
				// SchedulerException("ConnectionProvider class '" + cpClass
				// + "' could not be instantiated.", e);
				// throw initException;
				// }
				//
				// try {
				// // remove the class name, so it isn't attempted to be set
				// pp.getUnderlyingProperties().remove(
				// StdSchedulerFactory.PROP_CONNECTION_PROVIDER_CLASS);
				//
				// setBeanProps(cp, pp.getUnderlyingProperties());
				// cp.initialize();
				// } catch (Exception e) {
				// initException = new
				// SchedulerException("ConnectionProvider class '" + cpClass
				// + "' props could not be configured.", e);
				// throw initException;
				// }
				//
				// dbMgr = DBConnectionManager.getInstance();
				// dbMgr.addConnectionProvider(dsNames[i], cp);
			//} else {
				String dsJndi = pp.getStringProperty(
						StdSchedulerFactory.PROP_DATASOURCE_JNDI_URL, null);

				if (dsJndi != null) {
					boolean dsAlwaysLookup = pp
							.getBooleanProperty(StdSchedulerFactory.PROP_DATASOURCE_JNDI_ALWAYS_LOOKUP);
					String dsJndiInitial = pp
							.getStringProperty(StdSchedulerFactory.PROP_DATASOURCE_JNDI_INITIAL);
					String dsJndiProvider = pp
							.getStringProperty(StdSchedulerFactory.PROP_DATASOURCE_JNDI_PROVDER);
					String dsJndiPrincipal = pp
							.getStringProperty(StdSchedulerFactory.PROP_DATASOURCE_JNDI_PRINCIPAL);
					String dsJndiCredentials = pp
							.getStringProperty(StdSchedulerFactory.PROP_DATASOURCE_JNDI_CREDENTIALS);
					Properties props = null;
					if (null != dsJndiInitial || null != dsJndiProvider
							|| null != dsJndiPrincipal
							|| null != dsJndiCredentials) {
						props = new Properties();
						if (dsJndiInitial != null) {
							props.put(
									StdSchedulerFactory.PROP_DATASOURCE_JNDI_INITIAL,
									dsJndiInitial);
						}
						if (dsJndiProvider != null) {
							props.put(
									StdSchedulerFactory.PROP_DATASOURCE_JNDI_PROVDER,
									dsJndiProvider);
						}
						if (dsJndiPrincipal != null) {
							props.put(
									StdSchedulerFactory.PROP_DATASOURCE_JNDI_PRINCIPAL,
									dsJndiPrincipal);
						}
						if (dsJndiCredentials != null) {
							props.put(
									StdSchedulerFactory.PROP_DATASOURCE_JNDI_CREDENTIALS,
									dsJndiCredentials);
						}
					}
					JNDIConnectionProvider cp = new JNDIConnectionProvider(
							dsJndi, props, dsAlwaysLookup);
					dbMgr = DBConnectionManager.getInstance();
					dbMgr.addConnectionProvider(dsNames[i], cp);
				} else {
					String dsDriver = pp
							.getStringProperty(PoolingConnectionProvider.DB_DRIVER);
					String dsURL = pp
							.getStringProperty(PoolingConnectionProvider.DB_URL);

					if (dsDriver == null) {
						log.warn("Driver not specified for DataSource: "
								+ dsNames[i]);
						return false;
					}
					if (dsURL == null) {
						log.warn("DB URL not specified for DataSource: "
								+ dsNames[i]);
						return false;
					}
				}
				try {
					BasicConnectionProvider cp = new BasicConnectionProvider(
							pp.getUnderlyingProperties());
					
					// set 16s for connection timeout
					cp.setCheckoutTimeout(16 * 1000);
					
					dbMgr = DBConnectionManager.getInstance();
					dbMgr.addConnectionProvider(dsNames[i], cp);
					
				} catch (SQLException sqle) {
					log.warn("Could not initialize DataSource: " + dsNames[i],
							sqle);
					return false;
				} catch (SchedulerException e) {
					log.warn(e.getLocalizedMessage());
					return false;
				}
			//}
			
			// try to connect
			try {
				DBConnectionManager.getInstance().getConnection(dsNames[i]);
				
			} catch (SQLException e) {
				log.warn("Cloud not connect: " + e.getLocalizedMessage());
				return false;
			} catch (Exception e) {
				log.warn("Cloud not connect: " + e.getLocalizedMessage());
				return false;
			}
		}
		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		log.trace("run cluster monitor job");
		
		/**
		 * get node configuration
		 */
		String requestedFile = System.getProperty(SystemConfig.sQuartz);
		String propFileName = requestedFile != null ? requestedFile
				: "./config/quartz_node.properties";
		File propFile = new File(propFileName);
		Properties props = new Properties();
		InputStream in = null;

		try {
			if (propFile.exists()) {
				in = new BufferedInputStream(new FileInputStream(propFileName));
				props.load(in);
			}
			if (!initDataSources(props)) {
				// db still can not be connected, continue running in-memory mode
				log.info("db still can not be connected, continue running in-memory mode");
			} else {
				// now database is online, notify observers
				VCloudPlusEventsCenter.Instance().TriggerEvents(VCloudPlusEvents.VCloudPlusDBIsOnLine);
				VCloudPlusEventsCenter.Instance().notifyObservers(VCloudPlusEvents.VCloudPlusDBIsOnLine, Boolean.TRUE);
			}
		} catch (FileNotFoundException e) {
			log.warn(e.getLocalizedMessage());
	
		} catch (IOException e) {
			log.warn(e.getLocalizedMessage());
			
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignore) { /* ignore */
				}
			}
		}
		
		log.trace("end of cluster monitor job");
	}

}
