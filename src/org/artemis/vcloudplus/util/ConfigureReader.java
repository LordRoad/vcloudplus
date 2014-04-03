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
package org.artemis.vcloudplus.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.quartz.utils.PropertiesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ConfigureReader read configure files
 * ConfigureReader.java is written at Mar 28, 2014
 * @author junli
 */
public class ConfigureReader {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * read properties
	 * @param iConfigurePath
	 * @return null if not existed
	 */
	public Properties Read(String iConfigurePath) {
		
		File propFile = new File(iConfigurePath);
		Properties props = new Properties();
		InputStream in = null;

		try {
			if (propFile.exists()) {
				in = new BufferedInputStream(new FileInputStream(iConfigurePath));
				props.load(in);
			}
		} catch (FileNotFoundException e) {
			log.warn(e.getLocalizedMessage());
			return null;
		} catch (IOException e) {
			log.warn(e.getLocalizedMessage());
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignore) { /* ignore */
				}
			}
		}
		
		return props;
	}
	
	/**
	 * get group from configure file
	 * @param iConfigurePath
	 * @param iGroup
	 * @return null, if not existed
	 */
	public PropertiesParser GetGroup(String iConfigurePath, String iGroup) {
		Properties lProperties = Read(iConfigurePath);
		if (lProperties != null) {
			return GetGroup(lProperties, iGroup);
		}
		return null;
	}
	
	/**
	 * get group property 
	 * @param iProperties
	 * @param iGroup, group name
	 * @return
	 */
	public PropertiesParser GetGroup(Properties iProperties, String iGroup) {
		PropertiesParser cfg = new PropertiesParser(iProperties);
		PropertiesParser lPropertiesParser = new PropertiesParser(
				cfg.getPropertyGroup(iGroup, true));
		
		return lPropertiesParser;
	}
	
}
