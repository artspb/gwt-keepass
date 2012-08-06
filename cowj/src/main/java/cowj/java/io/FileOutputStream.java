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

import java.io.IOException;
import cowj.java.nio.ByteBuffer;
import cowj.java.nio.channels.FileChannel;
import cowj.org.apache.harmony.nio.Util;
import cowj.org.apache.harmony.nio.internal.IOUtil;

/**
 * A specialized {@link cowj.java.io.OutputStream} that writes to a file in the file system.
 * All write requests made by calling methods in this class are directly
 * forwarded to the equivalent function of the underlying operating system.
 * Since this may induce some performance penalty, in particular if many small
 * write requests are made, a FileOutputStream is often wrapped by a
 * BufferedOutputStream.
 * 
 * @see BufferedOutputStream
 * @see cowj.java.io.FileInputStream
 */
public class FileOutputStream extends OutputStream implements Closeable {
    private FileChannel channel;

    /**
     * Constructs a new FileOutputStream on the File {@code file}. If the file
     * exists, it is overwritten.
     * 
     * @param file
     *            the file to which this stream writes.
     * @throws FileNotFoundException
     *             if {@code file} cannot be opened for writing.
     * @throws SecurityException
     *             if a {@code SecurityManager} is installed and it denies the
     *             write request.
     * @see SecurityManager#checkWrite(FileDescriptor)
     */
    public FileOutputStream(File file) throws FileNotFoundException {
        this(file, false);
    }

    /**
     * Constructs a new FileOutputStream on the File {@code file}. The
     * parameter {@code append} determines whether or not the file is opened and
     * appended to or just opened and overwritten.
     * 
     * @param file
     *            the file to which this stream writes.
     * @param append
     *            indicates whether or not to append to an existing file.
     * @throws FileNotFoundException
     *             if the {@code file} cannot be opened for writing.
     * @throws SecurityException
     *             if a {@code SecurityManager} is installed and it denies the
     *             write request.
     * @see SecurityManager#checkWrite(FileDescriptor)
     * @see SecurityManager#checkWrite(String)
     */
    public FileOutputStream(File file, boolean append)
            throws FileNotFoundException {
        super();
        channel = file.createChannel();
        try
        {
	        if (append)
	        	channel.position(channel.size());
        }
        catch (IOException e)
        {
        	try
        	{
        		channel.close();
        	}
        	catch (IOException e1)
        	{
        	}
        	
        	throw new FileNotFoundException();
        }
    }

    /**
     * Constructs a new FileOutputStream on the file named {@code filename}. If
     * the file exists, it is overwritten. The {@code filename} may be absolute
     * or relative to the system property {@code "user.dir"}.
     * 
     * @param filename
     *            the name of the file to which this stream writes.
     * @throws FileNotFoundException
     *             if the file cannot be opened for writing.
     * @throws SecurityException
     *             if a {@code SecurityManager} is installed and it denies the
     *             write request.
     */
    public FileOutputStream(String filename) throws FileNotFoundException {
        this(filename, false);
    }

    /**
     * Constructs a new FileOutputStream on the file named {@code filename}.
     * The parameter {@code append} determines whether or not the file is opened
     * and appended to or just opened and overwritten. The {@code filename} may
     * be absolute or relative to the system property {@code "user.dir"}.
     * 
     * @param filename
     *            the name of the file to which this stream writes.
     * @param append
     *            indicates whether or not to append to an existing file.
     * @throws FileNotFoundException
     *             if the file cannot be opened for writing.
     * @throws SecurityException
     *             if a {@code SecurityManager} is installed and it denies the
     *             write request.
     */
    public FileOutputStream(String filename, boolean append)
            throws FileNotFoundException {
        this(new File(filename), append);
    }

    /**
     * Closes this stream. This implementation closes the underlying operating
     * system resources allocated to represent this stream.
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
     * Frees any resources allocated for this stream before it is garbage
     * collected. This method is called from the Java Virtual Machine.
     * 
     * @throws java.io.IOException
     *             if an error occurs attempting to finalize this stream.
     */
    @Override
    protected void finalize() throws IOException {
        close();
    }

    /**
     * Returns the FileChannel equivalent to this output stream.
     * <p>
     * The file channel is write-only and has an initial position within the
     * file that is the same as the current position of this stream within the
     * file. All changes made to the underlying file descriptor state via the
     * channel are visible by the output stream and vice versa.
     *
     * @return the file channel representation for this stream.
     */
    public FileChannel getChannel() {
        return channel;
    }

    /**
     * Writes the entire contents of the byte array {@code buffer} to this
     * stream.
     * 
     * @param buffer
     *            the buffer to be written to the file.
     * @throws java.io.IOException
     *             if this stream is closed or an error occurs attempting to
     *             write to this stream.
     */
    @Override
    public void write(byte[] buffer) throws IOException {
        write(buffer, 0, buffer.length);
    }

    /**
     * Writes {@code count} bytes from the byte array {@code buffer} starting at
     * {@code offset} to this stream.
     * 
     * @param buffer
     *            the buffer to write to this stream.
     * @param offset
     *            the index of the first byte in {@code buffer} to write.
     * @param count
     *            the number of bytes from {@code buffer} to write.
     * @throws IndexOutOfBoundsException
     *             if {@code count < 0} or {@code offset < 0}, or if
     *             {@code count + offset} is greater than the length of
     *             {@code buffer}.
     * @throws java.io.IOException
     *             if this stream is closed or an error occurs attempting to
     *             write to this stream.
     * @throws NullPointerException
     *             if {@code buffer} is {@code null}.
     */
    @Override
    public void write(byte[] buffer, int offset, int count) throws IOException {
        if (buffer == null) {
            throw new NullPointerException();
        }
        if (count < 0 || offset < 0 || offset > buffer.length
                || count > buffer.length - offset) {
            throw new IndexOutOfBoundsException();
        }

        if (count == 0) {
            return;
        }

        openCheck();
		
		ByteBuffer bb = ByteBuffer.wrap(buffer, offset, count);
		channel.write(bb);
    }

    /**
     * Writes the specified byte {@code oneByte} to this stream. Only the low
     * order byte of the integer {@code oneByte} is written.
     * 
     * @param oneByte
     *            the byte to be written.
     * @throws java.io.IOException
     *             if this stream is closed an error occurs attempting to write
     *             to this stream.
     */
    @Override
    public void write(int oneByte) throws IOException {
        openCheck();
		
		ByteBuffer bb = ByteBuffer.allocate(1);
		bb.put(0, (byte) oneByte);
		channel.write(bb);
    }

    private synchronized void openCheck() throws IOException {
		if (!channel.isOpen())
			throw new IOException();
    }
}
