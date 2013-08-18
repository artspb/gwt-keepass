/*
 * Copyright 2009 Lukasz Wozniak
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 * http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package pl.sind.keepass.util;

import org.gwttime.time.DateTime;

import java.util.Date;

/**
 * Utility class to handle binary data.<br>
 * 
 * @author Lukasz Wozniak
 * 
 */
public class Utils {
	/**
	 * Unpacks date stored in kdb format.
	 * 
	 * @param d
	 * @return
	 */
	public static Date unpackDate(byte[] d) {
		// Byte bits: 00000000 11111111 22222222 33333333 44444444
		// Contents : 00YYYYYY YYYYYYMM MMDDDDDH HHHHMMMM MMSSSSSS
		int year = (d[0] << 6) | ((d[1] >> 2) & 0x0000003F);
		int month = ((d[1] & 0x00000003) << 2) | ((d[2] >> 6) & 0x00000003);
		int day = (d[2] >> 1) & 0x0000001F;
		int hour = ((d[2] & 0x00000001) << 4) | ((d[3] >> 4) & 0x0000000F);
		int minute = ((d[3] & 0x0000000F) << 2) | ((d[4] >> 6) & 0x00000003);
		int second = d[4] & 0x0000003F;
        DateTime dateTime = new DateTime(year, month, day, hour, minute, second);
        return dateTime.toDate();
	}

	public static byte[] packDate(Date date) {
        DateTime dateTime = new DateTime(date);
		// Byte bits: 00000000 11111111 22222222 33333333 44444444
		// Contents : 00YYYYYY YYYYYYMM MMDDDDDH HHHHMMMM MMSSSSSS
		byte[] bytes = new byte[5];
		int s = dateTime.getSecondOfMinute();
		int m = dateTime.getMinuteOfHour();
		int h = dateTime.getHourOfDay();
		int d = dateTime.getDayOfMonth();
		int mm = dateTime.getMonthOfYear() + 1;
		int y = dateTime.getYear();

		bytes[4] = (byte) ((m << 6) | s); 
		bytes[3] = (byte) ((m >> 2 )| (h<<4));
		bytes[2] = (byte) ((h>>4) | (d<<1) | (mm <<6));
		bytes[1] = (byte) ((mm>>2) | (y<<2));
		bytes[0] = (byte) (y>>6);
		
		return bytes;
	}

	/**
	 * Creates byte array representation of HEX string.<br>
	 * 
	 * @param s
	 *            string to parse
	 * @return
	 */
	public static byte[] fromHexString(String s) {
		int length = s.length() / 2;
		byte[] bytes = new byte[length];
		for (int i = 0; i < length; i++) {
			bytes[i] = (byte) ((Character.digit(s.charAt(i * 2), 16) << 4)
                    | Character.digit(s.charAt((i * 2) + 1), 16));
		}
		return bytes;
	}

	/**
	 * Creates HEX String representation of supplied byte array.<br/>
	 * Each byte is represented by a double character element from 00 to ff
	 * 
	 * @param fieldData
	 *            to be tringed
	 * @return
	 */
	public static String toHexString(byte[] fieldData) {
		StringBuilder sb = new StringBuilder();
        for (byte aFieldData : fieldData) {
            int v = (aFieldData & 0xFF);
            if (v <= 0xF) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(v));
        }
		return sb.toString();
	}

	public static int bytesToInt(byte[] data) {
		int value=(data[3] & 0xff);
		value =value << 8;
		value|=(data[2] & 0xff);
		value <<= 8;
		value|=(data[1] & 0xff);
		value <<= 8;
		value|=(data[0] & 0xff);
		return value;
	}
	
	public static byte[] intTobytes(int value) {
		byte[] bytes = new byte[4];
		intTobytes(value, bytes);
		return bytes;
	}

	public static void intTobytes(int value, byte[] bytes) {
		bytes[3] = (byte) (value >> 24 & 0xff);
		bytes[2] = (byte) (value >> 16 & 0xff);
		bytes[1] = (byte) (value >> 8 & 0xff);
		bytes[0] = (byte) (value & 0xff);
	}
	
	public static void shortTobytes(short value, byte[] bytes) {
		bytes[1] = (byte) (value >> 8 & 0xff);
		bytes[0] = (byte) (value & 0xff);
	}

	public static short bytesToShort(byte[] data) {
		short value=(short) (data[1] & 0xff);
		value <<= 8;
		value|=(data[0] & 0xff);
		return value;
	}
}
