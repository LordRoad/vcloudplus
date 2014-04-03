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

import java.sql.SQLException;
import java.util.Properties;

import org.quartz.SchedulerException;
import org.quartz.utils.PoolingConnectionProvider;

/**
 * BasicConnectionProvider extend more configuration from <code>PoolingConnectionProvider<code>
 * 
 * @see PoolingConnectionProvider
 * 
 * BasicConnectionProvider.java is written at Apr 1, 2014
 * @author junli
 */
public class BasicConnectionProvider extends PoolingConnectionProvider {

	/**
	 * @param config
	 * @throws SchedulerException
	 * @throws SQLException
	 */
	public BasicConnectionProvider(Properties config)
			throws SchedulerException, SQLException {
		super(config);
		
	}

	/**
	 * @param dbDriver
	 * @param dbURL
	 * @param dbUser
	 * @param dbPassword
	 * @param maxConnections
	 * @param dbValidationQuery
	 * @throws SQLException
	 * @throws SchedulerException
	 */
	public BasicConnectionProvider(String dbDriver, String dbURL,
			String dbUser, String dbPassword, int maxConnections,
			String dbValidationQuery) throws SQLException, SchedulerException {
		super(dbDriver, dbURL, dbUser, dbPassword, maxConnections,
				dbValidationQuery);

	}
	
	public void setCheckoutTimeout(int checkoutTimeout) {
		this.getDataSource().setCheckoutTimeout(checkoutTimeout);
	}
	
}
