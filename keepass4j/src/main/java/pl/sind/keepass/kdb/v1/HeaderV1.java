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

import cowj.java.nio.ByteBuffer;

public class HeaderV1 {
    public static int LENGTH = 124;
    int dwSignature1;
    int dwSignature2;
    int dwFlags;
    int dwVersion;
    byte[] aMasterSeed;
    byte[] aEncryptionIV;
    int dwGroups;
    int dwEntries;
    byte[] aContentsHash;
    byte[] aMasterSeed2;
    int dwKeyEncRounds;

    public HeaderV1(ByteBuffer bb) {
        bb.rewind();
        dwSignature1 = bb.getInt();
        dwSignature2 = bb.getInt();
        dwFlags = bb.getInt();
        dwVersion = bb.getInt();
        bb.get(aMasterSeed = new byte[16]);
        bb.get(aEncryptionIV = new byte[16]);
        dwGroups = bb.getInt();
        dwEntries = bb.getInt();
        bb.get(aContentsHash = new byte[32]);
        bb.get(aMasterSeed2 = new byte[32]);
        dwKeyEncRounds = bb.getInt();
    }

    public int getSignature1() {
        return dwSignature1;
    }

    public int getSignature2() {
        return dwSignature2;
    }

    public int getFlags() {
        return dwFlags;
    }

    public int getVersion() {
        return dwVersion;
    }

    public byte[] getMasterSeed() {
        return aMasterSeed;
    }

    public byte[] getEncryptionIV() {
        return aEncryptionIV;
    }

    public int getGroups() {
        return dwGroups;
    }

    public int getEntries() {
        return dwEntries;
    }

    public byte[] getContentsHash() {
        return aContentsHash;
    }

    public byte[] getMasterSeed2() {
        return aMasterSeed2;
    }

    public int getKeyEncRounds() {
        return dwKeyEncRounds;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("dwVersion=").append(Integer.toHexString(this.dwVersion));
        sb.append(", ");
        sb.append("dwGroups=").append(this.dwGroups);
        sb.append(", ");
        sb.append("dwEntries=").append(this.dwEntries);
        sb.append(", ");
        sb.append("dwKeyEncRounds=").append(this.dwKeyEncRounds);
        sb.append('}');
        return sb.toString();
    }
}
