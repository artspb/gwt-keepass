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

package cowj.java.io;

import java.io.FileOutputStream;
import java.io.IOException;
import cowj.java.nio.ByteBuffer;
import cowj.java.nio.channels.FileChannel;
import cowj.org.apache.harmony.Messages;

/**
 * A specialized {@link InputStream} that reads from a file in the file system.
 * All read requests made by calling methods in this class are directly
 * forwarded to the equivalent function of the underlying operating system.
 * Since this may induce some performance penalty, in particular if many small
 * read requests are made, a FileInputStream is often wrapped by a
 * BufferedInputStream.
 * 
 * @see BufferedInputStream
 * @see java.io.FileOutputStream
 */
public class FileInputStream extends InputStream implements Closeable {
    private FileChannel channel;

    /**
     * Constructs a new {@code FileInputStream} based on {@code file}.
     * 
     * @param file
     *            the file from which this stream reads.
     * @throws FileNotFoundException
     *             if {@code file} does not exist.
     * @throws SecurityException
     *             if a {@code SecurityManager} is installed and it denies the
     *             read request.
     */
    public FileInputStream(File file) throws FileNotFoundException {
        super();
        if (file == null) {
            // luni.4D=Argument must not be null
            throw new NullPointerException(Messages.getString("luni.4D")); //$NON-NLS-1$
        }
        channel = file.createChannel();
    }

    /**
     * Constructs a new {@code FileInputStream} on the file named
     * {@code fileName}. The path of {@code fileName} may be absolute or
     * relative to the system property {@code "user.dir"}.
     * 
     * @param fileName
     *            the path and name of the file from which this stream reads.
     * @throws FileNotFoundException
     *             if there is no file named {@code fileName}.
     * @throws SecurityException
     *             if a {@code SecurityManager} is installed and it denies the
     *             read request.
     */
    public FileInputStream(String fileName) throws FileNotFoundException {
        this(null == fileName ? (File) null : new File(fileName));
    }

    /**
     * Returns the number of bytes that are available before this stream will
     * block. This method always returns the size of the file minus the current
     * position.
     * 
     * @return the number of bytes available before blocking.
     * @throws java.io.IOException
     *             if an error occurs in this stream.
     */
    @Override
    public int available() throws IOException {
        openCheck();
        return (int)(channel.size() - channel.position());
    }

    /**
     * Closes this stream.
     * 
     * @throws java.io.IOException
     *             if an error occurs attempting to close this stream.
     */
    @Override
    public void close() throws IOException {
        if (channel != null) {
            synchronized (channel) {
                if (channel.isOpen()) {
                    channel.close();
                }
            }
        }
    }

    /**
     * Ensures that all resources for this stream are released when it is about
     * to be garbage collected.
     * 
     * @throws java.io.IOException
     *             if an error occurs attempting to finalize this stream.
     */
    @Override
    protected void finalize() throws IOException {
        close();
    }

    /**
     * Returns the {@link FileChannel} equivalent to this input stream.
     * <p>
     * The file channel is read-only and has an initial position within the file
     * that is the same as the current position of this stream within the file.
     * All changes made to the underlying file descriptor state via the channel
     * are visible by the input stream and vice versa.
     *
     * @return the file channel for this stream.
     */
    public FileChannel getChannel() {
        return channel;
    }

    /**
     * Reads a single byte from this stream and returns it as an integer in the
     * range from 0 to 255. Returns -1 if the end of this stream has been
     * reached.
     * 
     * @return the byte read or -1 if the end of this stream has been reached.
     * @throws java.io.IOException
     *             if this stream is closed or another I/O error occurs.
     */
    @Override
    public int read() throws IOException {
        openCheck();
    	ByteBuffer bb = ByteBuffer.allocate(1);
    	int result = channel.read(bb);
    	if (result == 0)
    		return -1;
    	else
    		return bb.get(0) & 0xff;
    }

    /**
     * Reads bytes from this stream and stores them in the byte array
     * {@code buffer}.
     * 
     * @param buffer
     *            the byte array in which to store the bytes read.
     * @return the number of bytes actually read or -1 if the end of the stream
     *         has been reached.
     * @throws java.io.IOException
     *             if this stream is closed or another I/O error occurs.
     */
    @Override
    public int read(byte[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }

    /**
     * Reads at most {@code count} bytes from this stream and stores them in the
     * byte array {@code buffer} starting at {@code offset}.
     * 
     * @param buffer
     *            the byte array in which to store the bytes read.
     * @param offset
     *            the initial position in {@code buffer} to store the bytes read
     *            from this stream.
     * @param count
     *            the maximum number of bytes to store in {@code buffer}.
     * @return the number of bytes actually read or -1 if the end of the stream
     *         has been reached.
     * @throws IndexOutOfBoundsException
     *             if {@code offset < 0} or {@code count < 0}, or if
     *             {@code offset + count} is greater than the size of
     *             {@code buffer}.
     * @throws java.io.IOException
     *             if the stream is closed or another IOException occurs.
     */
    @Override
    public int read(byte[] buffer, int offset, int count) throws IOException {
        if (count > buffer.length - offset || count < 0 || offset < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (0 == count) {
            return 0;
        }
        openCheck();
        
        ByteBuffer bb = ByteBuffer.wrap(buffer, offset, count);
        return (int) channel.read(bb);
    }

    /**
     * Skips {@code count} number of bytes in this stream. Subsequent
     * {@code read()}'s will not return these bytes unless {@code reset()} is
     * used. This method may perform multiple reads to read {@code count} bytes.
     * 
     * @param count
     *            the number of bytes to skip.
     * @return the number of bytes actually skipped.
     * @throws java.io.IOException
     *             if {@code count < 0}, this stream is closed or another
     *             IOException occurs.
     */
    @Override
    public long skip(long count) throws IOException {
        openCheck();

        if (count == 0) {
            return 0;
        }
        if (count < 0) {
            // luni.AC=Number of bytes to skip cannot be negative
            throw new IOException(Messages.getString("luni.AC")); //$NON-NLS-1$
        }

        long max = channel.size();
        long current = channel.position();
        long updated = Math.min(max, current+count);
        channel.position(updated);
        
        return updated - current;
    }

    private synchronized void openCheck() throws IOException {
    	if (!channel.isOpen())
            throw new IOException();
    }
}
