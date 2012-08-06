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

import cowj.java.nio.ByteBuffer;
import cowj.java.nio.ByteOrder;
import pl.sind.keepass.exceptions.KeePassDataBaseException;
import pl.sind.keepass.exceptions.UnsupportedDataBaseException;
import pl.sind.keepass.kdb.v1.KeePassDataBaseV1;
import pl.sind.keepass.kdb.v2.KeePassDataBaseV2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KeePassDataBaseFactory {
    private static final List<DBType> TYPES;
    private static final String V1 = "pl.sind.keepass.kdb.v1.KeePassDataBaseV1";
    private static final String V2 = "pl.sind.keepass.kdb.v2.KeePassDataBaseV2";

    static {
        // XKDB is xml based dont want to load class when unnecesary as its
        // dependencies may not be available if someone intends to use kdb only
        ArrayList<DBType> list = new ArrayList<DBType>();
        list.add(new DBType(KeePassConst.KDB_SIG_1, KeePassConst.KDB_SIG_2, V1));
        list.add(new DBType(KeePassConst.KDBX_SIG_1, KeePassConst.KDBX_SIG_2, V2));
        TYPES = Collections.unmodifiableList(list);
    }

    public static KeePassDataBase loadDataBase(byte[] db, byte[] key, String password) throws KeePassDataBaseException {
        String c = identifyDataBase(db);
        return instantiate(c, db, key, password);
    }

    private static KeePassDataBase instantiate(String clazz, byte[] db, byte[] key, String password) throws KeePassDataBaseException {
        if (V1.equals(clazz)) {
            return new KeePassDataBaseV1(db, key, password);
        }
        if (V2.equals(clazz)) {
            return new KeePassDataBaseV2(db, key, password);
        }

        throw new UnsupportedDataBaseException("Database with class " + clazz + " is not supported");
    }

    public static void saveDataBase(KeePassDataBase dataBase, byte[] db, byte[] key, String password) {
        // TODO Auto-generated method stub
    }

    private static String identifyDataBase(byte[] db) throws UnsupportedDataBaseException {
        ByteBuffer bb = ByteBuffer.wrap(db);
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
