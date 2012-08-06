package pl.sind.keepass;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import pl.sind.keepass.util.Utils;

@RunWith(JUnit4.class)
public class UtilTest {

	byte[] referenceBytes = new byte[] { 0x01, -49, -22, -75, -128, -75, 127,
			88, -74, -122, 67, -41, 59, 24, 22, -101, -79, 88, -35, 111, 123,
			-115, 40, 68, -75, -69, -34, -9, 98, 101, 0x00, (byte) 0xe3 };

	String referenceString = "01cfeab580b57f58b68643d73b18169bb158dd6f7b8d2844b5bbdef7626500e3";

	@Test
	public void checkToHex() throws Exception {
		String ret = Utils.toHexString(referenceBytes);
		assertEquals(referenceString,ret);
	}

	@Test
	public void checkFromHex() throws Exception {
		byte[] ret = Utils.fromHexString(referenceString);
		assertArrayEquals(referenceBytes, ret);
	}

	@Test
	public void checkHexRoundTrip() throws Exception {
		Random r = new Random();
		byte[] source = new byte[r.nextInt(512)+128];
		r.nextBytes(source);
		String testString = Utils.toHexString(source);
		byte[] result = Utils.fromHexString(testString);
		assertArrayEquals(source, result);
	}
	
	
	@Test
	public void checkIntTrip() throws Exception {
		Random r = new Random();
		int source =513; 
//			(int)4294967294L;
		System.out.println(Integer.toHexString((int)source));
		byte[] intTobytes = Utils.intTobytes((int)source);
		System.out.println(Arrays.toString(intTobytes));
		System.out.println(Utils.toHexString(intTobytes));
		
		int bytesToInt = Utils.bytesToInt(intTobytes);
		assertEquals(source, bytesToInt);
		
	}
	
	@Test
	public void checkDateTrip() throws Exception {
		Calendar c = Calendar.getInstance();
		//packing looses miliseconds
		c.set(Calendar.MILLISECOND, 000);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.DAY_OF_MONTH, 31);
		c.set(Calendar.MONTH, 11);
		c.set(Calendar.YEAR, 2010);
		
		Date start = c.getTime();
		
		byte[] packDate = Utils.packDate(start);
		Date end = Utils.unpackDate(packDate);
		
		System.out.println(start);
		System.out.println(end);
		c.setTime(start);
		System.err.println(c.getTimeInMillis());
		c.setTime(end);
		System.err.println(c.getTimeInMillis());
		System.out.println(start.equals(end));
		assertEquals(start, end);
		
	}

}
