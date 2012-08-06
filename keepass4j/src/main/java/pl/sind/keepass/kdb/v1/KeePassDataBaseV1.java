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
package pl.sind.keepass.kdb.v1;

import cowj.java.io.InputStream;
import cowj.java.nio.ByteBuffer;
import cowj.java.nio.ByteOrder;
import pl.sind.keepass.crypto.Cipher;
import pl.sind.keepass.crypto.CipherException;
import pl.sind.keepass.crypto.CipherFactory;
import pl.sind.keepass.exceptions.DecryptionFailedException;
import pl.sind.keepass.exceptions.KeePassDataBaseException;
import pl.sind.keepass.exceptions.UnsupportedDataBaseException;
import pl.sind.keepass.hash.Hash;
import pl.sind.keepass.hash.HashFactory;
import pl.sind.keepass.kdb.KeePassDataBase;
import pl.sind.keepass.util.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static pl.sind.keepass.kdb.KeePassConst.*;

/**
 * KDB file database format reader.
 * Supports password, keyfile and password+keyfile database access.
 * 
 * @author Lukasz Wozniak
 *
 */
public class KeePassDataBaseV1 implements KeePassDataBase {
	// private HeaderV1 header;
	private byte[] keyFileHash;
	private byte[] passwordHash;
	private Hash hash;
	private List<Entry> entries;
	private List<Group> groups;
	private Cipher cipher;
	private int keyEncRounds;
	private byte[] masterSeed;
	private byte[] masterSeed2;
	private byte[] encryptionIv;

	public KeePassDataBaseV1(byte[] data, byte[] keyFile, String password) throws KeePassDataBaseException {
		super();

		ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

		HeaderV1 header = new HeaderV1(bb);
		validateHeader(header);
		hash = HashFactory.getHash(Hash.SHA_256);
		cipher = CipherFactory.getCipher(Cipher.AES);

		keyEncRounds = header.getKeyEncRounds();
		setPassword(password);
		setKeyFile(keyFile);
		if (this.passwordHash == null && this.keyFileHash == null) {
			throw new KeePassDataBaseException(
					"Password and key file cannot be both null");
		}

		byte[] content = new byte[data.length - bb.position()];
		bb.get(content, 0, content.length);

		masterSeed = header.getMasterSeed();
		masterSeed2 = header.getMasterSeed2();
		encryptionIv = header.getEncryptionIV();
		byte[] result = null;
		try {
			result = decrypt(content);
		} catch (CipherException e) {
			throw new KeePassDataBaseException("Unable to decrypt database.", e);
		}

		if (!Arrays.equals(hash.hash(result), header.getContentsHash())) {
			throw new DecryptionFailedException(
					"Data corrupted or invalid password or key file.");
		}

		this.entries = new ArrayList<Entry>();
		this.groups = new ArrayList<Group>();
		try {
			bb = ByteBuffer.wrap(result).order(ByteOrder.LITTLE_ENDIAN);
			GroupDeserializer groupsDes = new GroupDeserializer();
			for (int i = 0; i < header.getGroups(); i++) {
				short fieldType;
				while ((fieldType = bb.getShort()) != GroupFieldTypes.TERMINATOR) {
//					System.out.println(String.format("Group fieldType %x", fieldType));
					if (fieldType == 0) {
						continue;
					}
					int fieldSize = bb.getInt();
					groupsDes.readField(fieldType, fieldSize, bb);
				}
				bb.getInt(); // reading FIELDSIZE of group entry terminator
				groups.add(groupsDes.getGroup());
				groupsDes.reset();
			}
			EntryDeserializer entryDes = new EntryDeserializer();
			for (int i = 0; i < header.getEntries(); i++) {
				short fieldType;
				
				while ((fieldType = bb.getShort()) != GroupFieldTypes.TERMINATOR) {
					if (fieldType == 0) {
						continue;
					}
					int fieldSize = bb.getInt();
					entryDes.readField(fieldType, fieldSize, bb);
				}
				bb.getInt(); // reading FIELDSIZE of entry terminator
				entries.add(entryDes.getEntry());
				entryDes.reset();
			}
		} catch (UnsupportedEncodingException e) {
			// weird...
			throw new KeePassDataBaseException(
					"UTF-8 is not supported on this platform");
		}

	}

	private byte[] decrypt(byte[] content) throws CipherException {
		return cipher.decrypt(prepareKey(), content, encryptionIv);
	}

	private byte[] prepareKey() throws CipherException {
		byte[] passwordKey;
		if (keyFileHash == null) {
			passwordKey = passwordHash;
		} else if (passwordHash == null) {
			passwordKey = keyFileHash;
		} else {
			passwordKey = passwordHash;
			hash.reset();
			hash.update(passwordHash);
			hash.update(keyFileHash);
			passwordKey = hash.digest();
		}

		byte[] masterKey = cipher.encrypt(masterSeed2, passwordKey, null,
				keyEncRounds, false);
		masterKey = hash.hash(masterKey);
		hash.reset();
		hash.update(masterSeed);
		hash.update(masterKey);
		masterKey = hash.digest();
		return masterKey;
	}

	public void setPassword(String password) {
		if (password != null) {
			this.passwordHash = hash.hash(password.getBytes());
		} else {
			this.passwordHash = null;
		}
	}

    public void setKeyFile(byte[] keyFile) throws KeePassDataBaseException {
        if (keyFile != null) {
            int length = keyFile.length;
            switch (length) {
                case 32:
                    System.arraycopy(keyFile, 0, keyFileHash, 0, length);
                    return;
                case 64:
                    keyFileHash = Utils.fromHexString(new String(keyFile, 0, 64));
                    return;
                default:
                    hash.reset();
                    hash.update(keyFile, 0, length);
                    keyFileHash = hash.digest();
            }
        } else {
            keyFileHash = null;
        }
    }

	/**
	 * Validates version and encryption flags.<br>
	 * 
	 * @throws UnsupportedDataBaseException
	 */
	private void validateHeader(HeaderV1 header)
			throws UnsupportedDataBaseException {
		if (header.getVersion() != KDB_FILE_VERSION) {
			throw new UnsupportedDataBaseException("Invalid database version " + header.getVersion() + ". Only " + KDB_FILE_VERSION + " version is supported");
		}
		int flags = header.getFlags();
		if ((flags & KDB_FLAG_TWOFISH) == KDB_FLAG_TWOFISH) {
			throw new UnsupportedDataBaseException(
					"Twofish algorithm is not supported");
		}

		if ((flags & KDB_FLAG_ARC4) == KDB_FLAG_ARC4) {
			throw new UnsupportedDataBaseException(
					"Twofish algorithm is not supported");
		}

	}

	public List<Entry> getEntries() {
		return entries;
	}

	public List<Group> getGroups() {
		return groups;
	}

}
