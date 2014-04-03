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
package org.artemis.vcloudplus.run;

import org.artemis.vcloudplus.en.VCloudPlusEvents;

/**
 * Observable the default observer/observable classes are not suitable for our application.
 * we need to implement it and i'd like to keep the same interface with default realization
 * 
 * use default observer/observable implementation. it's better for extend
 * 
 * @see java.util.Observable
 * 
 * ObservableSet.java is written at Mar 31, 2014
 * @author junli
 * @deprecated
 */
@Deprecated
public abstract class ObservableSet {
	
	public abstract void addObserver(VCloudPlusEvents e, Observer o);

	public abstract void deleteObserver(VCloudPlusEvents e, Observer o);
	
	public abstract void notifyObservers();
	
	public abstract void notifyObservers(VCloudPlusEvents e, Object arg);
	
	public abstract void deleteObservers();
	
	public abstract void deleteObserver(VCloudPlusEvents e);
	
	protected abstract void setChanged(VCloudPlusEvents e);
	
	protected abstract void clearChanged(VCloudPlusEvents e);
	
	protected abstract boolean hasChanged(VCloudPlusEvents e);
	
	protected abstract int countObservers();
	
	protected abstract int countObservers(VCloudPlusEvents e);
	
}
