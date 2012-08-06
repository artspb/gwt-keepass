/*
 *  This file is redistributable under the Apache License, Version 2.0
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

package cowj.java.util;

public class Locale
{
	public static Locale defaultLocale = new Locale("");
	
	public static final Locale CANADA = defaultLocale;
	public static final Locale CANADA_FRENCH = defaultLocale;
	public static final Locale CHINA = defaultLocale;
	public static final Locale CHINESE = defaultLocale;
	public static final Locale ENGLISH = defaultLocale;
	public static final Locale FRANCE = defaultLocale;
	public static final Locale FRENCH = defaultLocale;
	public static final Locale GERMAN = defaultLocale;
	public static final Locale GERMANY = defaultLocale;
	public static final Locale ITALIAN = defaultLocale;
	public static final Locale ITALY = defaultLocale;
	public static final Locale JAPAN = defaultLocale;
	public static final Locale JAPANESE = defaultLocale;
	public static final Locale KOREA = defaultLocale;
	public static final Locale KOREAN = defaultLocale;
	public static final Locale PRC = defaultLocale;
	public static final Locale ROOT = defaultLocale;
	public static final Locale SIMPLIFIED_CHINESE = defaultLocale;
	public static final Locale TAIWAN = defaultLocale;
	public static final Locale TRADITIONAL_CHINESE = defaultLocale;
	public static final Locale UK = defaultLocale;
	public static final Locale US = defaultLocale;

	public Locale(String language)
	{
	}

	public Locale(String language, String country)
	{
	}

	public Locale(String language, String country, String variant)
	{
	}


	public static Locale[] getAvailableLocales()
	{
		return new Locale[] { defaultLocale };
	}

	public String getCountry()
	{
		return "";
	}

	public static Locale getDefault()
	{
		return defaultLocale;
	}

	public String getDisplayCountry()
	{
		return "";
	}

	public String getDisplayCountry(Locale inLocale)
	{
		return "";
	}

	public String getDisplayLanguage()
	{
		return "";
	}

	public String getDisplayLanguage(Locale inLocale)
	{
		return "";
	}

	public String getDisplayName()
	{
		return "";
	}

	public String getDisplayName(Locale inLocale)
	{
		return "";
	}

	public String getDisplayVariant()
	{
		return "";
	}

	public String getDisplayVariant(Locale inLocale)
	{
		return "";
	}

	public String getISO3Country()
	{
		return "";
	}

	public String getISO3Language()
	{
		return "";
	}

	public static String[] getISOCountries()
	{
		return new String[] {};
	}

	public static String[] getISOLanguages()
	{
		return new String[] {};
	}

	public String getLanguage()
	{
		return "";
	}

	public String getVariant()
	{
		return "";
	}

	public static void setDefault(Locale newLocale)
	{
		defaultLocale = newLocale;
	}
}
