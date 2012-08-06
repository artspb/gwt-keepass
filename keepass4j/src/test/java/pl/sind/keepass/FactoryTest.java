package pl.sind.keepass;

import cowj.java.io.ByteArrayOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import pl.sind.keepass.exceptions.KeePassDataBaseException;
import pl.sind.keepass.kdb.KeePassDataBase;
import pl.sind.keepass.kdb.KeePassDataBaseFactory;
import pl.sind.keepass.kdb.v1.KeePassDataBaseV1;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class FactoryTest {
    @Test
    public void openPasswordKdb() throws Exception {
        InputStream inputStream = FactoryTest.class.getResourceAsStream("testing-pass.kdb");

        KeePassDataBase db = KeePassDataBaseFactory.loadDataBase(inputStreamToBytes(inputStream), null, "testing");
        assertNotNull(db);
    }

    @Test
    public void openKeyFileKdb() throws Exception {
        InputStream inputStream = FactoryTest.class.getResourceAsStream("testing-key.kdb");
        byte[] db = inputStreamToBytes(inputStream);
        inputStream = FactoryTest.class.getResourceAsStream("testing-key.key");

        KeePassDataBase keePassDb = KeePassDataBaseFactory.loadDataBase(db, inputStreamToBytes(inputStream), null);
        KeePassDataBaseV1 kdb1 = (KeePassDataBaseV1) keePassDb;
        System.out.println(kdb1.getGroups());
        System.out.println(kdb1.getEntries());
        assertNotNull(keePassDb);
    }

    @Test
    public void openKeyFilePasswordKdb() throws Exception {
        InputStream inputStream = FactoryTest.class.getResourceAsStream("testing-pass-key.kdb");
        byte[] db = inputStreamToBytes(inputStream);
        inputStream = FactoryTest.class.getResourceAsStream("testing-key.key");

        KeePassDataBase keePassDb = KeePassDataBaseFactory.loadDataBase(db, inputStreamToBytes(inputStream), "testing");
        KeePassDataBaseV1 kdb1 = (KeePassDataBaseV1) keePassDb;
        System.out.println(kdb1.getGroups());
        System.out.println(kdb1.getEntries());
        assertNotNull(keePassDb);
    }


    @Test(expected = KeePassDataBaseException.class)
    public void openNullsKdb() throws Exception {
        InputStream inputStream = FactoryTest.class.getResourceAsStream("testing-pass.kdb");

        KeePassDataBaseFactory.loadDataBase(inputStreamToBytes(inputStream), null, null);
    }

    @Test(expected = RuntimeException.class)
    public void openPasswordXkdb() throws Exception {
        InputStream inputStream = FactoryTest.class.getResourceAsStream("testing-pass.kdbx");

        KeePassDataBaseFactory.loadDataBase(inputStreamToBytes(inputStream), null, "testing");
    }

    private byte[] inputStreamToBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int read = inputStream.read(bytes);
        while (read > 0) {
            outputStream.write(bytes, 0, read);
            read = inputStream.read(bytes);
        }
        bytes = outputStream.toByteArray();
        return bytes;
    }
}
