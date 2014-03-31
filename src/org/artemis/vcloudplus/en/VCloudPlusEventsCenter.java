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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observer;

import org.artemis.vcloudplus.run.ObservableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * VCloudPlusEventsCenter events center to trigger some events or notify some changes.
 * i think current it's ok to put all events in this realization
 * 
 * VCloudPlusEventsCenter.java is written at Mar 31, 2014
 * @author junli
 */
@SuppressWarnings("deprecation")
public class VCloudPlusEventsCenter extends ObservableSet {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * check if status of current event has been changed and do synchronization 
	 */
	private Map<VCloudPlusEvents, Boolean> mChanged = 
			new HashMap<VCloudPlusEvents, Boolean>();
	
	private Map<VCloudPlusEvents,HashSet<Observer>> mObservers = 
			new HashMap<VCloudPlusEvents, HashSet<Observer>>();
	
	private static VCloudPlusEventsCenter sVCloudPlusEventsCenter = new VCloudPlusEventsCenter();
	
	private VCloudPlusEventsCenter() {
		
	}
	
	public static VCloudPlusEventsCenter Instance() {
		synchronized (sVCloudPlusEventsCenter) {
			return sVCloudPlusEventsCenter;
		}
	}

	public static void Delete() {
		synchronized (sVCloudPlusEventsCenter) {
			sVCloudPlusEventsCenter = null;
		}
	}

	@Override
	public void addObserver(VCloudPlusEvents e, Observer o) {
		if (mChanged.containsKey(e)) {
			synchronized(mChanged.get(e)) {
				if (mObservers.containsKey(e)) {
					mObservers.get(e).add(o);
				} else {
					HashSet<Observer> lObserverSet = new HashSet<Observer>(); 
					lObserverSet.add(o);
					mObservers.put(e, lObserverSet);
				}
			}
		} else {
			mChanged.put(e, Boolean.FALSE);
			addObserver(e, o);
		}
	}

	@Override
	public void deleteObserver(VCloudPlusEvents e, Observer o) {
		if (mChanged.containsKey(e)) {
			synchronized(mChanged.get(e)) {
				if (mObservers.containsKey(e) &&
						mObservers.get(e).contains(o)) {
					mObservers.get(e).remove(o);
				} 
			}
		}
	}

	@Override
	public void notifyObservers() {
		for (VCloudPlusEvents iVCloudPlusEvents : mObservers.keySet()) {
			notifyObservers(iVCloudPlusEvents, null);
		}
	}

	@Override
	public void notifyObservers(VCloudPlusEvents e, Object arg) {
		
	}
	
	@Override
	public void deleteObserver(VCloudPlusEvents e) {
		if (mChanged.containsKey(e)) {
			synchronized(mChanged.get(e)) {
				if (mObservers.containsKey(e)) {
					mObservers.get(e).clear();
				}
			}
		}
	}

	@Override
	public void deleteObservers() {
		for (VCloudPlusEvents e : mChanged.keySet()) {
			deleteObserver(e);
		}
	}

	@Override
	protected void setChanged(VCloudPlusEvents e) {
		if (mChanged.containsKey(e)) {
			synchronized(mChanged.get(e)) {
				mChanged.put(e, Boolean.TRUE);
			}
		}
	}

	@Override
	protected void clearChanged() {
		
	}

	@Override
	protected boolean hasChanged(VCloudPlusEvents e) {
		if (mChanged.containsKey(e)) {
			return mChanged.get(e).booleanValue();
		}
		return false;
	}

	@Override
	protected int countObservers() {
		
	}	
	
}
