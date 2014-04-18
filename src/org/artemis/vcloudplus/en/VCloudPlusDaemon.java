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
package org.artemis.vcloudplus.en;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.artemis.vcloudplus.common.SystemConfig;
import org.artemis.vcloudplus.en.VCloudPlusRunner.VCloudPlusRunningMode;
import org.artemis.vcloudplus.task.ClusterMgt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * VCloudPlusDaemon daemon support
 * VCloudPlusDaemon.java is written at Apr 16, 2014
 * @author junli
 */
public class VCloudPlusDaemon implements Daemon{
	private final Logger log = LoggerFactory.getLogger(getClass());
	private Thread mVCloudPlusRunningContainer = null;
	private VCloudPlusDaemonRunner mVCloudPlusDaemonRunner = null;
	
	// for windows platform prunsrv
	private static VCloudPlusDaemon sVCloudPlusDaemon = new VCloudPlusDaemon();
	
	/**
	 * prunsrv runs in different mode (see http://commons.apache.org/proper/commons-daemon/procrun.html)
	 * jvm will be run in the same process with prunsrv, Java or exe will be launched in separate process. 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String cmd = "start";
        if (args.length > 0) {
            cmd = args[0];
        }
        if ("start".equals(cmd)) {
        	VCloudPlusDaemon.windowsStart(args);
        }
        else {
        	VCloudPlusDaemon.windowsStop(args);
        }
	}
	
	/**
	 * Static methods called by prunsrv to start/stop the Windows service.  Pass the argument "start"
     * to start the service, and pass "stop" to stop the service. 
     * 
     * @param args
	 * @throws Exception 
	 */
	public static void windowsStart(String [] args) throws Exception {
		synchronized (sVCloudPlusDaemon) {
			if (!sVCloudPlusDaemon.isShutdown()) {
				sVCloudPlusDaemon.stop();
			}
			sVCloudPlusDaemon.init(null);
			sVCloudPlusDaemon.start();
		}
	}
	
	public static void windowsStop(String [] args) throws Exception {
		synchronized (sVCloudPlusDaemon) {
			if (!sVCloudPlusDaemon.isShutdown()) {
				sVCloudPlusDaemon.stop();
			}
		}
	}
	
	public boolean isShutdown() {
		return mVCloudPlusRunningContainer == null;
	}
	
	@Override
	public void destroy() {
		if (mVCloudPlusRunningContainer != null) {
			mVCloudPlusRunningContainer.interrupt();
			mVCloudPlusRunningContainer = null;
			mVCloudPlusDaemonRunner = null;
		}
		log.warn("vcloud plus manager is destroyed!");
	}

	@Override
	public void init(DaemonContext arg0) throws DaemonInitException, Exception {
		mVCloudPlusDaemonRunner = new VCloudPlusDaemonRunner();
		mVCloudPlusRunningContainer = new Thread(mVCloudPlusDaemonRunner);
		mVCloudPlusRunningContainer.setUncaughtExceptionHandler(new ExceptionHandler());
	}
	
	private class ExceptionHandler implements UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			log.error(t.getName() + " throws uncaught exception: " + e.getLocalizedMessage());
		}
		
	}
	
	private class VCloudPlusDaemonRunner implements Runnable {
		private VCloudPlusRunner mVCloudPlusRunner = null;
		private boolean mRunning = true;
		private Thread mVCloudPlusThread = null;
		
		public void shutdown() {
			mRunning = false;
			if (mVCloudPlusRunner != null) {
				mVCloudPlusRunner.shutdown();
			}
			if (mVCloudPlusThread != null) {
				mVCloudPlusThread.interrupt();
			}
			synchronized (this) {
				this.notifyAll();
			}
		}
		
		@Override
		public void run() {
			try {
				String lLog4JConfig = System.getProperty(SystemConfig.sLog4J);
				if (lLog4JConfig == null)
				{
					System.setProperty(SystemConfig.sLog4J, 
							SystemConfig.sDefaultLog4JPath);
				}
				
				VCloudPlusRunningMode lVCloudPlusRunningMode = VCloudPlusRunningMode.Cluster;
				try {
					ClusterMgt lTestingClusterMgt = new ClusterMgt();
					lTestingClusterMgt.execute(null);
					if (!lTestingClusterMgt.isDBOnline()) {
						lVCloudPlusRunningMode = VCloudPlusRunningMode.Memory;
					}
				} catch (Exception e) {
					e.printStackTrace();
					lVCloudPlusRunningMode = VCloudPlusRunningMode.Memory;
				}
				
				String lQuartzConfig;
				if (lVCloudPlusRunningMode == VCloudPlusRunningMode.Cluster) {
					lQuartzConfig = System.getProperty(SystemConfig.sQuartz);
					if (lQuartzConfig == null)
					{
						System.setProperty(SystemConfig.sQuartz, 
								SystemConfig.sDefaultQuartzPath);
					}
				} else {
					lQuartzConfig = System.getProperty(SystemConfig.sQuartz);
					if (lQuartzConfig != null) {
						System.setProperty(SystemConfig.sQuartzBak, lQuartzConfig);
						System.clearProperty(SystemConfig.sQuartz);
					}
				}
				
				String lVCloudPlusConfig = System.getProperty(SystemConfig.sVCloudPlus);
				if (lVCloudPlusConfig == null)
				{
					System.setProperty(SystemConfig.sVCloudPlus, 
							SystemConfig.sDefaultVCloudPlusPath);
				}
				
				// assert lVCloudPlusRunningMode != VCloudPlusRunningMode.Cluster;
				
				while (mRunning) {
					mVCloudPlusRunner = new VCloudPlusRunner();
					mVCloudPlusRunner.SetRunningMode(lVCloudPlusRunningMode);
					
					//lVCloudPlusRunner.EnableDebug();
					
					mVCloudPlusThread = new Thread(mVCloudPlusRunner);
					mVCloudPlusThread.run();
					while (mVCloudPlusThread.isAlive() && mRunning)
					{
						try {
							mVCloudPlusThread.join();
						} catch (InterruptedException e) {
							// ignore
						}
					}	
					mVCloudPlusThread = null;
					if (!mRunning) {
						break;
					}
					
					switch (mVCloudPlusRunner.HasPersistenceError()) {
					case NONE:
					{
						mRunning = false;
						break;
					}
					case NeedInCluster:
					{
						lQuartzConfig = System.getProperty(SystemConfig.sQuartz);
						if (lQuartzConfig == null)
						{
							lQuartzConfig = System.getProperty(SystemConfig.sQuartzBak);
							if (lQuartzConfig != null) {
								System.setProperty(SystemConfig.sQuartz, lQuartzConfig);
							} else {
								System.setProperty(SystemConfig.sQuartz, SystemConfig.sDefaultQuartzPath);
							}
						}
						lVCloudPlusRunningMode = VCloudPlusRunningMode.Cluster;
						break;
					}
					case NeedInMemory:
					{
						System.clearProperty(SystemConfig.sQuartz);
						lVCloudPlusRunningMode = VCloudPlusRunningMode.Memory;
						break;
					}
					}
					
				}
				log.info("vcloud plus manager is shut down");
			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
			} finally {
				
			}
		}
	}

	@Override
	public void start() throws Exception {
		if (mVCloudPlusRunningContainer != null) {
			log.info("vcloud plus manager is going to run");
			mVCloudPlusRunningContainer.start();
		} else {
			log.error("vcloud plus manager is not initialized!");
			throw new Exception("vcloud plus manager is not initialized!");
		}
	}

	@Override
	public void stop() throws Exception {
		if (mVCloudPlusDaemonRunner != null) {
			mVCloudPlusDaemonRunner.shutdown();
			mVCloudPlusRunningContainer.join(3000);
		}
		mVCloudPlusRunningContainer = null;
		mVCloudPlusDaemonRunner = null;
		log.info("vcloud plus manager is shutdown");
	}

}
