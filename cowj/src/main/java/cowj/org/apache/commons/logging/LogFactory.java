/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cowj.org.apache.commons.logging;

public class LogFactory implements Log
{
	static private Log _instance = new LogFactory();
	
	/**
	 * <p>
	 * Construct (if necessary) and return a <code>Log</code> instance, using
	 * the factory's current set of configuration attributes.
	 * </p>
	 * 
	 * <p>
	 * <strong>NOTE</strong> - Depending upon the implementation of the
	 * <code>LogFactory</code> you are using, the <code>Log</code> instance you
	 * are returned may or may not be local to the current application, and may
	 * or may not be returned again on a subsequent call with the same name
	 * argument.
	 * </p>
	 * 
	 * @param name
	 *            Logical name of the <code>Log</code> instance to be returned
	 *            (the meaning of this name is only known to the underlying
	 *            logging implementation that is being wrapped)
	 * 
	 * @exception LogConfigurationException
	 *                if a suitable <code>Log</code> instance cannot be returned
	 */
	public Log getInstance(String name)
	{
		return _instance;
	}
	
	/**
	 * Convenience method to return a named logger, without the application
	 * having to care about factories.
	 * 
	 * @param clazz
	 *            Class from which a log name will be derived
	 * 
	 * @exception LogConfigurationException
	 *                if a suitable <code>Log</code> instance cannot be returned
	 */
	public static Log getLog(Class clazz)
	{
		return _instance;
	}
	
	/** Do nothing */
	public void trace(Object message)
	{
	}
	
	/** Do nothing */
	public void trace(Object message, Throwable t)
	{
	}
	
	/** Do nothing */
	public void debug(Object message)
	{
	}
	
	/** Do nothing */
	public void debug(Object message, Throwable t)
	{
	}
	
	/** Do nothing */
	public void info(Object message)
	{
	}
	
	/** Do nothing */
	public void info(Object message, Throwable t)
	{
	}
	
	/** Do nothing */
	public void warn(Object message)
	{
	}
	
	/** Do nothing */
	public void warn(Object message, Throwable t)
	{
	}
	
	/** Do nothing */
	public void error(Object message)
	{
	}
	
	/** Do nothing */
	public void error(Object message, Throwable t)
	{
	}
	
	/** Do nothing */
	public void fatal(Object message)
	{
	}
	
	/** Do nothing */
	public void fatal(Object message, Throwable t)
	{
	}
	
	/**
	 * Debug is never enabled.
	 * 
	 * @return false
	 */
	public final boolean isDebugEnabled()
	{
		return false;
	}
	
	/**
	 * Error is never enabled.
	 * 
	 * @return false
	 */
	public final boolean isErrorEnabled()
	{
		return false;
	}
	
	/**
	 * Fatal is never enabled.
	 * 
	 * @return false
	 */
	public final boolean isFatalEnabled()
	{
		return false;
	}
	
	/**
	 * Info is never enabled.
	 * 
	 * @return false
	 */
	public final boolean isInfoEnabled()
	{
		return false;
	}
	
	/**
	 * Trace is never enabled.
	 * 
	 * @return false
	 */
	public final boolean isTraceEnabled()
	{
		return false;
	}
	
	/**
	 * Warn is never enabled.
	 * 
	 * @return false
	 */
	public final boolean isWarnEnabled()
	{
		return false;
	}
}
