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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.artemis.vcloudplus.run.ObservableSet;
import org.artemis.vcloudplus.run.Observer;

/**
 * VCloudPlusEventsCenter events center to trigger some events or notify some changes.
 * i think current it's ok to put all events in this realization
 * 
 * VCloudPlusEventsCenter.java is written at Mar 31, 2014
 * @author junli
 */
@SuppressWarnings("deprecation")
public class VCloudPlusEventsCenter extends ObservableSet {
	
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

	/**
	 * set event to changed status. Usually, caller will call notifyObservers
	 * @see notifyObservers#method(VCloudPlusEvents, Object)
	 * @param e
	 */
	public void TriggerEvents(VCloudPlusEvents e) {
		setChanged(e);
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
		if (mChanged.containsKey(e)) {
			Object[] lObserverArray;
			Boolean lChanged = mChanged.get(e);
			synchronized(lChanged) {
				if (lChanged == Boolean.FALSE) {
					return;
				}
				lObserverArray = mObservers.get(e).toArray();
				clearChanged(e);
			}
			for (int iter = 0; iter < lObserverArray.length; ++iter) {
				if (lObserverArray[iter] != null) {
					((Observer)lObserverArray[iter]).update(this, arg);
				}
			}
		}
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
	protected void clearChanged(VCloudPlusEvents e) {
		if (mChanged.containsKey(e)) {
			synchronized(mChanged.get(e)) {
				mChanged.put(e, Boolean.FALSE);
			}
		}
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
		Iterator<Entry<VCloudPlusEvents, HashSet<Observer>>> lObserverIterator = 
				mObservers.entrySet().iterator();
		int lTotalCount = 0;
		while (lObserverIterator.hasNext()) {
			lTotalCount += lObserverIterator.next().getValue().size();
		}
		return lTotalCount;
	}

	@Override
	protected int countObservers(VCloudPlusEvents e) {
		if (mChanged.containsKey(e)) {
			return mObservers.get(e).size();
		}
		return 0;
	}
	
}
