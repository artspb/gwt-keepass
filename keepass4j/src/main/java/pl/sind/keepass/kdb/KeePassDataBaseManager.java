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

import cowj.java.io.File;
import cowj.java.io.FileInputStream;
import cowj.java.io.FileNotFoundException;
import cowj.java.io.FileOutputStream;
import java.io.IOException;
import cowj.java.io.InputStream;
import cowj.java.io.OutputStream;

import pl.sind.keepass.exceptions.KeePassDataBaseException;
import pl.sind.keepass.exceptions.UnsupportedDataBaseException;

public abstract class KeePassDataBaseManager {

	public static KeePassDataBase openDataBase(File dbFile, File keyFile,
			String password) throws IOException, UnsupportedDataBaseException,
			KeePassDataBaseException {
		return openDataBase(new FileInputStream(dbFile), keyFile == null ? null
				: new FileInputStream(keyFile), password);
	}

	public static KeePassDataBase openDataBase(InputStream dbFile,
			InputStream keyFile, String password)
			throws UnsupportedDataBaseException, IOException,
			KeePassDataBaseException {
		return KeePassDataBaseFactory.loadDataBase(dbFile, keyFile, password);
	}

	public static void saveDataBase(KeePassDataBase dataBase, File dbFile,
			File keyFile, String password) throws FileNotFoundException {
		saveDataBase(dataBase, new FileOutputStream(dbFile),
				keyFile == null ? null : new FileInputStream(keyFile), password);

	}

	public static void saveDataBase(KeePassDataBase dataBase,
			OutputStream dbFile, InputStream keyFile, String password) {
		KeePassDataBaseFactory
				.saveDataBase(dataBase, dbFile, keyFile, password);

	}
}
