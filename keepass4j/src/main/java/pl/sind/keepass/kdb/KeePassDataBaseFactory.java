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
package pl.sind.keepass.kdb;

import cowj.java.io.ByteArrayOutputStream;
import java.io.IOException;
import cowj.java.io.InputStream;
import cowj.java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import cowj.java.nio.ByteBuffer;
import cowj.java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.sind.keepass.exceptions.KeePassDataBaseException;
import pl.sind.keepass.exceptions.UnsupportedDataBaseException;

public class KeePassDataBaseFactory {
	private static final List<DBType> TYPES;

	static {
		// XKDB is xml based dont want to load class when unnecesary as its
		// dependencies may not be available if someone intends to use kdb only
		ArrayList<DBType> list = new ArrayList<DBType>();
		list.add(new DBType(KeePassConst.KDB_SIG_1, KeePassConst.KDB_SIG_2,
				"pl.sind.keepass.kdb.v1.KeePassDataBaseV1"));
		list.add(new DBType(KeePassConst.KDBX_SIG_1, KeePassConst.KDBX_SIG_2,
				"pl.sind.keepass.kdb.v2.KeePassDataBaseV2"));
		TYPES = Collections.unmodifiableList(list);

	}

	public static KeePassDataBase loadDataBase(InputStream dbFile,
			InputStream keyFile, String password) throws IOException,
			UnsupportedDataBaseException, KeePassDataBaseException {
		// first load database
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] bytes = new byte[1024];
		int read = dbFile.read(bytes);
		while (read > 0) {
			bos.write(bytes, 0, read);
			read = dbFile.read(bytes);
		}
		bytes = bos.toByteArray();
		
		String c = identifyDataBase(bytes);
		return instantiate(c, bytes, keyFile, password);
	}

	private static KeePassDataBase instantiate(String c, byte[] data,
			InputStream keyFile, String password)
			throws UnsupportedDataBaseException, KeePassDataBaseException {
		try {
			Class<?> clazz = Class.forName(c);
			Constructor<?> constructor = clazz.getConstructor(new Class<?>[] {
					byte[].class, InputStream.class, String.class });
			return (KeePassDataBase) constructor.newInstance(new Object[] { data,
					keyFile, password });
		} catch (InvocationTargetException e) {
			if (e.getCause() != null) {
				if (e.getCause() instanceof UnsupportedDataBaseException) {
					throw (UnsupportedDataBaseException) e.getCause();
				} else if (e.getCause() instanceof KeePassDataBaseException) {
					throw (KeePassDataBaseException) e.getCause();
				}
			}
			throw new RuntimeException(e);

		} catch (Exception e) {
			// well... never should happen
			throw new RuntimeException(e);
		}
	}

	public static void saveDataBase(KeePassDataBase dataBase,
			OutputStream dbFile, InputStream keyFile, String password) {
		// TODO Auto-generated method stub

	}

	private static String identifyDataBase(byte[] data)
			throws UnsupportedDataBaseException {
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		int dwSignature1 = bb.getInt();
		int dwSignature2 = bb.getInt();
		for (DBType type : TYPES) {
			if (type.getSig1() == dwSignature1
					&& type.getSig2() == dwSignature2) {
				return type.getClazz();
			}
		}
		throw new UnsupportedDataBaseException("Database with signature " + dwSignature1 + ":" + dwSignature2 + " is not supported");
	}

	private static class DBType {
		private int sig1;
		private int sig2;
		private String clazz;

		public DBType(int kdbSig1, int kdbSig2, String clazz) {
			this.sig1 = kdbSig1;
			this.sig2 = kdbSig2;
			this.clazz = clazz;
		}

		public int getSig1() {
			return sig1;
		}

		public int getSig2() {
			return sig2;
		}

		public String getClazz() {
			return clazz;
		}

	}

}
