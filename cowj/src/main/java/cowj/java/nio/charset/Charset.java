/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cowj.java.nio.charset;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import cowj.java.nio.ByteBuffer;
import cowj.java.nio.CharBuffer;
import cowj.java.nio.charset.codec.ASCII;
import cowj.java.nio.charset.codec.Cp1252;
import cowj.java.nio.charset.codec.ISO8859_1;
import cowj.java.nio.charset.codec.ISO8859_15;
import cowj.java.nio.charset.codec.UTF8;
import cowj.java.util.Locale;

/**
 * A charset defines a mapping between a Unicode character sequence and a byte
 * sequence. It facilitates the encoding from a Unicode character sequence into
 * a byte sequence, and the decoding from a byte sequence into a Unicode
 * character sequence.
 * <p>
 * A charset has a canonical name, which is usually in uppercase. Typically it
 * also has one or more aliases. The name string can only consist of the
 * following characters: '0' - '9', 'A' - 'Z', 'a' - 'z', '.', ':'. '-' and '_'.
 * The first character of the name must be a digit or a letter.
 * <p>
 * The following charsets should be supported by any java platform: US-ASCII,
 * ISO-8859-1, UTF-8, UTF-16BE, UTF-16LE, UTF-16.
 * <p>
 * Additional charsets can be made available by configuring one or more charset
 * providers through provider configuration files. Such files are always named
 * as "java.nio.charset.spi.CharsetProvider" and located in the
 * "META-INF/services" sub folder of one or more classpaths. The files should be
 * encoded in "UTF-8". Each line of their content specifies the class name of a
 * charset provider which extends
 * <code>java.nio.charset.spi.CharsetProvider</code>. A line should end with
 * '\r', '\n' or '\r\n'. Leading and trailing whitespaces are trimmed. Blank
 * lines, and lines (after trimming) starting with "#" which are regarded as
 * comments, are both ignored. Duplicates of names already found are also
 * ignored. Both the configuration files and the provider classes will be loaded
 * using the thread context class loader.
 * <p>
 * This class is thread-safe.
 * 
 * @see java.nio.charset.spi.CharsetProvider
 */
public abstract class Charset implements Comparable<Charset>
{
	private static TreeMap<String, Charset> _builtInCharsets = null;
	
	private final String canonicalName;
	
	// the aliases set
	private final HashSet<String> aliasesSet;
	
	static
	{
		_builtInCharsets = new TreeMap<String, Charset>();
		new ISO8859_1();
		new ISO8859_15();
		new ASCII();
		new UTF8();
		new Cp1252();
	}
	
	/**
	 * Constructs a <code>Charset</code> object. Duplicated aliases are ignored.
	 * 
	 * @param canonicalName
	 *            the canonical name of the charset.
	 * @param aliases
	 *            an array containing all aliases of the charset. May be null.
	 * @throws IllegalCharsetNameException
	 *             on an illegal value being supplied for either
	 *             <code>canonicalName</code> or for any element of
	 *             <code>aliases</code>.
	 */
	protected Charset(String canonicalName, String[] aliases)
	{
		if (null == canonicalName)
		{
			throw new NullPointerException();
		}
		// check whether the given canonical name is legal
		checkCharsetName(canonicalName);
		this.canonicalName = canonicalName;
		// check each alias and put into a set
		this.aliasesSet = new HashSet<String>();
		if (null != aliases)
		{
			for (int i = 0; i < aliases.length; i++)
			{
				checkCharsetName(aliases[i]);
				this.aliasesSet.add(aliases[i]);
			}
		}
		
		_builtInCharsets.put(canonicalName, this);
		for (String alias : aliases())
			_builtInCharsets.put(alias, this);
	}
	
	/*
	 * Checks whether a character is a special character that can be used in
	 * charset names, other than letters and digits.
	 */
	private static boolean isSpecial(char c)
	{
		return ('-' == c || '.' == c || ':' == c || '_' == c);
	}
	
	/*
	 * Checks whether a character is a letter (ascii) which are defined in the
	 * spec.
	 */
	private static boolean isLetter(char c)
	{
		return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
	}
	
	/*
	 * Checks whether a character is a digit (ascii) which are defined in the
	 * spec.
	 */
	private static boolean isDigit(char c)
	{
		return ('0' <= c && c <= '9');
	}
	
	/*
	 * Checks whether a given string is a legal charset name. The argument name
	 * should not be null.
	 */
	private static void checkCharsetName(String name)
	{
		// An empty string is illegal charset name
		if (name.length() == 0)
		{
			throw new IllegalCharsetNameException(name);
		}
		// The first character must be a letter or a digit
		// This is related to HARMONY-68 (won't fix)
		// char first = name.charAt(0);
		// if (!isLetter(first) && !isDigit(first)) {
		// throw new IllegalCharsetNameException(name);
		// }
		// Check the remaining characters
		int length = name.length();
		for (int i = 0; i < length; i++)
		{
			char c = name.charAt(i);
			if (!isLetter(c) && !isDigit(c) && !isSpecial(c))
			{
				throw new IllegalCharsetNameException(name);
			}
		}
	}
	
	/**
	 * Gets a map of all available charsets supported by the runtime.
	 * <p>
	 * The returned map contains mappings from canonical names to corresponding
	 * instances of <code>Charset</code>. The canonical names can be considered
	 * as case-insensitive.
	 * 
	 * @return an unmodifiable map of all available charsets supported by the
	 *         runtime
	 */
	@SuppressWarnings("unchecked")
	public static SortedMap<String, Charset> availableCharsets()
	{
		return Collections.unmodifiableSortedMap(_builtInCharsets);
	}
		
	/**
	 * Gets a <code>Charset</code> instance for the specified charset name.
	 * 
	 * @param charsetName
	 *            the canonical name of the charset or an alias.
	 * @return a <code>Charset</code> instance for the specified charset name.
	 * @throws IllegalCharsetNameException
	 *             if the specified charset name is illegal.
	 * @throws UnsupportedCharsetException
	 *             if the desired charset is not supported by this runtime.
	 */
	public static Charset forName(String charsetName)
	{
		Charset c = _builtInCharsets.get(charsetName);
		if (null == c)
		{
			throw new UnsupportedCharsetException(charsetName);
		}
		return c;
	}
	
	/**
	 * Determines whether the specified charset is supported by this runtime.
	 * 
	 * @param charsetName
	 *            the name of the charset.
	 * @return true if the specified charset is supported, otherwise false.
	 * @throws IllegalCharsetNameException
	 *             if the specified charset name is illegal.
	 */
	public static synchronized boolean isSupported(String charsetName)
	{
		return _builtInCharsets.containsKey(charsetName);
	}
	
	/**
	 * Determines whether this charset is a super set of the given charset.
	 * 
	 * @param charset
	 *            a given charset.
	 * @return true if this charset is a super set of the given charset, false
	 *         if it's unknown or this charset is not a superset of the given
	 *         charset.
	 */
	public abstract boolean contains(Charset charset);
	
	/**
	 * Gets a new instance of an encoder for this charset.
	 * 
	 * @return a new instance of an encoder for this charset.
	 */
	public abstract CharsetEncoder newEncoder();
	
	/**
	 * Gets a new instance of a decoder for this charset.
	 * 
	 * @return a new instance of a decoder for this charset.
	 */
	public abstract CharsetDecoder newDecoder();
	
	/**
	 * Gets the canonical name of this charset.
	 * 
	 * @return this charset's name in canonical form.
	 */
	public final String name()
	{
		return this.canonicalName;
	}
	
	/**
	 * Gets the set of this charset's aliases.
	 * 
	 * @return an unmodifiable set of this charset's aliases.
	 */
	public final Set<String> aliases()
	{
		return Collections.unmodifiableSet(this.aliasesSet);
	}
	
	/**
	 * Gets the name of this charset for the default locale.
	 * 
	 * <p>
	 * The default implementation returns the canonical name of this charset.
	 * Subclasses may return a localized display name.
	 * 
	 * @return the name of this charset for the default locale.
	 */
	public String displayName()
	{
		return this.canonicalName;
	}
	
	/**
	 * Gets the name of this charset for the specified locale.
	 * 
	 * <p>
	 * The default implementation returns the canonical name of this charset.
	 * Subclasses may return a localized display name.
	 * 
	 * @param l
	 *            a certain locale
	 * @return the name of this charset for the specified locale
	 */
	public String displayName(Locale l)
	{
		return this.canonicalName;
	}
	
	/**
	 * Indicates whether this charset is known to be registered in the IANA
	 * Charset Registry.
	 * 
	 * @return true if the charset is known to be registered, otherwise returns
	 *         false.
	 */
	public final boolean isRegistered()
	{
		return !canonicalName.startsWith("x-") //$NON-NLS-1$
		        && !canonicalName.startsWith("X-"); //$NON-NLS-1$
	}
	
	/**
	 * Returns true if this charset supports encoding, false otherwise.
	 * 
	 * @return true if this charset supports encoding, false otherwise.
	 */
	public boolean canEncode()
	{
		return true;
	}
	
	/**
	 * Encodes the content of the give character buffer and outputs to a byte
	 * buffer that is to be returned.
	 * <p>
	 * The default action in case of encoding errors is
	 * <code>CodingErrorAction.REPLACE</code>.
	 * 
	 * @param buffer
	 *            the character buffer containing the content to be encoded.
	 * @return the result of the encoding.
	 */
	public final ByteBuffer encode(CharBuffer buffer)
	{
		try
		{
			return this.newEncoder()
			        .onMalformedInput(CodingErrorAction.REPLACE)
			        .onUnmappableCharacter(CodingErrorAction.REPLACE).encode(
			                buffer);
			
		}
		catch (CharacterCodingException ex)
		{
			throw new Error(ex.getMessage(), ex);
		}
	}
	
	/**
	 * Encodes a string and outputs to a byte buffer that is to be returned.
	 * <p>
	 * The default action in case of encoding errors is
	 * <code>CodingErrorAction.REPLACE</code>.
	 * 
	 * @param s
	 *            the string to be encoded.
	 * @return the result of the encoding.
	 */
	public final ByteBuffer encode(String s)
	{
		return encode(CharBuffer.wrap(s));
	}
	
	/**
	 * Decodes the content of the specified byte buffer and writes it to a
	 * character buffer that is to be returned.
	 * <p>
	 * The default action in case of decoding errors is
	 * <code>CodingErrorAction.REPLACE</code>.
	 * 
	 * @param buffer
	 *            the byte buffer containing the content to be decoded.
	 * @return a character buffer containing the output of the decoding.
	 */
	public final CharBuffer decode(ByteBuffer buffer)
	{
		
		try
		{
			return this.newDecoder()
			        .onMalformedInput(CodingErrorAction.REPLACE)
			        .onUnmappableCharacter(CodingErrorAction.REPLACE).decode(
			                buffer);
			
		}
		catch (CharacterCodingException ex)
		{
			throw new Error(ex.getMessage(), ex);
		}
	}
	
	/*
	 * -------------------------------------------------------------------
	 * Methods implementing parent interface Comparable
	 * -------------------------------------------------------------------
	 */

	/**
	 * Compares this charset with the given charset. This comparation is based
	 * on the case insensitive canonical names of the charsets.
	 * 
	 * @param charset
	 *            the given object to be compared with.
	 * @return a negative integer if less than the given object, a positive
	 *         integer if larger than it, or 0 if equal to it.
	 */
	public final int compareTo(Charset charset)
	{
		return this.canonicalName.compareToIgnoreCase(charset.canonicalName);
	}
	
	/*
	 * -------------------------------------------------------------------
	 * Methods overriding parent class Object
	 * -------------------------------------------------------------------
	 */

	/**
	 * Determines whether this charset equals to the given object. They are
	 * considered to be equal if they have the same canonical name.
	 * 
	 * @param obj
	 *            the given object to be compared with.
	 * @return true if they have the same canonical name, otherwise false.
	 */
	@Override
	public final boolean equals(Object obj)
	{
		if (obj instanceof Charset)
		{
			Charset that = (Charset) obj;
			return this.canonicalName.equals(that.canonicalName);
		}
		return false;
	}
	
	/**
	 * Gets the hash code of this charset.
	 * 
	 * @return the hash code of this charset.
	 */
	@Override
	public final int hashCode()
	{
		return this.canonicalName.hashCode();
	}
	
	/**
	 * Gets a string representation of this charset. Usually this contains the
	 * canonical name of the charset.
	 * 
	 * @return a string representation of this charset.
	 */
	@Override
	public final String toString()
	{
		return "Charset[" + this.canonicalName + "]"; //$NON-NLS-1$//$NON-NLS-2$
	}
	
	/**
	 * Gets the system default charset from the virtual machine.
	 * 
	 * @return the default charset.
	 */
	public static Charset defaultCharset()
	{
		return Charset.forName("UTF-8");
	}
	
	/**
	 * A comparator that ignores case.
	 */
	static class IgnoreCaseComparator implements Comparator<String>
	{
		
		// the singleton
		private static Comparator<String> c = new IgnoreCaseComparator();
		
		/*
		 * Default constructor.
		 */
		private IgnoreCaseComparator()
		{
			// no action
		}
		
		/*
		 * Gets a single instance.
		 */
		public static Comparator<String> getInstance()
		{
			return c;
		}
		
		/*
		 * Compares two strings ignoring case.
		 */
		public int compare(String s1, String s2)
		{
			return s1.compareToIgnoreCase(s2);
		}
	}
}
