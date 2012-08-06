/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package cowj.org.apache.james.mime4j.storage;

import cowj.org.apache.commons.logging.Log;
import cowj.org.apache.commons.logging.LogFactory;

/**
 * Allows for a default {@link cowj.org.apache.james.mime4j.storage.StorageProvider} instance to be configured on an
 * application level.
 * <p>
 * The default instance can be set by either calling
 * {@link #setInstance(cowj.org.apache.james.mime4j.storage.StorageProvider)} when the application starts up or by
 * setting the system property
 * <code>cowj.org.apache.james.mime4j.defaultStorageProvider</code> to the class name
 * of a <code>StorageProvider</code> implementation.
 * <p>
 * If neither option is used or if the class instantiation fails this class
 * provides a pre-configured default instance.
 */
public class DefaultStorageProvider
{
	
	/** Value is <code>cowj.org.apache.james.mime4j.defaultStorageProvider</code> */
	public static final String DEFAULT_STORAGE_PROVIDER_PROPERTY = "cowj.org.apache.james.mime4j.defaultStorageProvider";
	
	private static Log log = LogFactory.getLog(DefaultStorageProvider.class);
	
	private static volatile StorageProvider instance = null;
	
	static
	{
		initialize();
	}
	
	private DefaultStorageProvider()
	{
	}
	
	/**
	 * Returns the default {@link cowj.org.apache.james.mime4j.storage.StorageProvider} instance.
	 * 
	 * @return the default {@link cowj.org.apache.james.mime4j.storage.StorageProvider} instance.
	 */
	public static StorageProvider getInstance()
	{
		return instance;
	}
	
	/**
	 * Sets the default {@link cowj.org.apache.james.mime4j.storage.StorageProvider} instance.
	 * 
	 * @param instance
	 *            the default {@link cowj.org.apache.james.mime4j.storage.StorageProvider} instance.
	 */
	public static void setInstance(StorageProvider instance)
	{
		if (instance == null)
		{
			throw new IllegalArgumentException();
		}
		
		DefaultStorageProvider.instance = instance;
	}
	
	private static void initialize()
	{
		instance = new MemoryStorageProvider();
	}
	
	// for unit tests only
	static void reset()
	{
		instance = null;
		initialize();
	}
	
}
