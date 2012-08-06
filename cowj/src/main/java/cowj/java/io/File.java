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
import java.util.HashMap;
import cowj.gwt.ByteStore;
import cowj.java.nio.ByteBuffer;
import cowj.java.nio.MappedByteBuffer;
import cowj.java.nio.channels.FileChannel;
import cowj.java.nio.channels.FileLock;
import cowj.java.nio.channels.ReadableByteChannel;
import cowj.java.nio.channels.WritableByteChannel;

/**
 * This is a heavily customised implementation of File for use on GWT and other
 * pure-Java systems. Files are stored in-memory; the file namespace is non-
 * hierarchical. You can create an anonymous file with the File() constructor.
 */

public class File implements Comparable<File>
{
	private static class FileChannelImpl extends FileChannel
	{
		private ByteStore _storage;
		private long _position;
		private boolean _closed;
		
		public FileChannelImpl(File file) throws FileNotFoundException
        {
	        if (!file.exists())
	        	throw new FileNotFoundException(file.getName());
			_storage = file.getStorage();
			_closed = false;
        }
		
		@Override
        public void force(boolean metadata) throws IOException
        {
        }

		@Override
        public FileLock lock(long position, long size, boolean shared)
                throws IOException
        {
			throw new UnsupportedOperationException();
        }

		@Override
        public MappedByteBuffer map(MapMode mode, long position, long size)
                throws IOException
        {
			throw new UnsupportedOperationException();
        }

		@Override
        public long position() throws IOException
        {
			return _position;
        }

		@Override
        public FileChannel position(long offset) throws IOException
        {
			_position = offset;
			return this;
        }

		@Override
        public long size() throws IOException
        {
			return _storage.length();
        }

		@Override
        public long transferFrom(ReadableByteChannel src, long position,
                long count) throws IOException
        {
			ByteBuffer bb = ByteBuffer.allocate((int)count);
			int i = src.read(bb);
			return write(bb, position);
        }

		@Override
        public long transferTo(long position, long count,
                WritableByteChannel target) throws IOException
        {
			ByteBuffer bb = ByteBuffer.allocate((int)count);
			int i = read(bb, position);
			return target.write(bb);
        }

		@Override
        public FileChannel truncate(long size) throws IOException
        {
			_storage.resize((int) size);
			return this;
        }

		@Override
        public FileLock tryLock(long position, long size, boolean shared)
                throws IOException
        {
			throw new UnsupportedOperationException();
        }

		@Override
        public int read(ByteBuffer buffer) throws IOException
        {
			int i = read(buffer, _position);
			_position += i;
			return i;
        }

		@Override
        public int read(ByteBuffer buffer, long position)
                throws IOException
        {
			int available = _storage.length();
			int pos = (int)position;
			int count = 0;
			
			while (buffer.hasRemaining() && (position < available))
			{
				byte b = _storage.get(pos);
				buffer.put(b);
				count++;
				pos++;
			}
			
			return count;
        }

		@Override
        public long read(ByteBuffer[] buffers, int offset, int length)
                throws IOException
        {
			long count = 0;
			for (int i = offset; i < (offset+length); i++)
				count += read(buffers[i]);

			return count;
        }

		@Override
		public int write(ByteBuffer src) throws IOException
		{
			int i = write(src, _position);
			_position += i;
			return i;
		}
		
		@Override
        public int write(ByteBuffer buffer, long position)
                throws IOException
        {
			int count = 0;
			int pos = (int)position;
			
			while (buffer.hasRemaining())
			{
				byte b = buffer.get();
				_storage.put(pos, b);
				count++;
				pos++;
			}
			
			return count;
        }

		@Override
        public long write(ByteBuffer[] buffers, int offset, int length)
                throws IOException
        {
			long count = 0;
			for (int i = offset; i < (offset+length); i++)
				count += write(buffers[i]);

			return count;
        }

		@Override
		public void close() throws IOException
		{
			_closed = true;
		}
		
		@Override
		public boolean isOpen()
		{
		    return !_closed;
		}
	}
	
	private static HashMap<String, ByteStore> _fileSystem = new HashMap<String, ByteStore>();
	private String _name = "";
	private ByteStore _storage;
	
	/**
	 * The system dependent file separator character.
	 */
	public static final char separatorChar = ';';
	
	/**
	 * The system dependent file separator string. The initial value of this
	 * field is the system property "file.separator".
	 */
	public static final String separator = String.valueOf(separatorChar);
	
	/**
	 * The system dependent path separator character.
	 */
	public static final char pathSeparatorChar = '/';
	
	/**
	 * The system dependent path separator string. The initial value of this
	 * field is the system property "path.separator".
	 */
	public static final String pathSeparator = String.valueOf(pathSeparatorChar);
	
	/**
	 * Constructs a new anonymous file.
	 */
	public File()
	{
		_name = "";
	}
	
	/**
	 * Constructs a new anonymous file containing the specified data.
	 */
	public File(ByteStore data)
	{
		_name = "";
		_storage = data;
	}
	
	/**
	 * Constructs a new file using the specified directory and name.
	 * 
	 * @param dir
	 *            the directory where the file is stored. Must be null.
	 * @param name
	 *            the file's name.
	 * @throws NullPointerException
	 *             if {@code name} is {@code null}.
	 */
	public File(File dir, String name)
	{
		if (dir != null)
			throw new UnsupportedOperationException("File paths must be absolute");
		
		if (name == null)
			throw new NullPointerException();
		
		_name = name;
	}
	
	/**
	 * Constructs a new file using the specified path.
	 * 
	 * @param path
	 *            the path to be used for the file.
	 */
	public File(String name)
	{
		this((File)null, name);
	}
	
	/**
	 * Constructs a new File using the specified directory path and file name,
	 * placing a path separator between the two.
	 * 
	 * @param dirPath
	 *            the path to the directory where the file is stored.
	 * @param name
	 *            the file's name.
	 * @throws NullPointerException
	 *             if {@code name} is {@code null}.
	 */
	public File(String dirPath, String name)
	{
		if ((dirPath != null) && !dirPath.isEmpty())
				throw new UnsupportedOperationException("File paths must be absolute");
		
		if (name == null)
			throw new NullPointerException();

		_name = name;
	}
	
	/**
	 * Lists the file system roots. The Java platform may support zero or more
	 * file systems, each with its own platform-dependent root. Further, the
	 * canonical pathname of any file on the system will always begin with one
	 * of the returned file system roots.
	 * 
	 * @return the array of file system roots.
	 */
	public static File[] listRoots()
	{
		return new File[] {};
	}
	
	/**
	 * Indicates whether the current context is allowed to read from this file.
	 * 
	 * @return {@code true} if this file can be read, {@code false} otherwise.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies the
	 *             read request.
	 */
	public boolean canRead()
	{
		return exists();
	}
	
	/**
	 * Indicates whether the current context is allowed to write to this file.
	 * 
	 * @return {@code true} if this file can be written, {@code false}
	 *         otherwise.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies the
	 *             write request.
	 */
	public boolean canWrite()
	{
		return true;
	}
	
	/**
	 * Returns the relative sort ordering of the paths for this file and the
	 * file {@code another}. The ordering is platform dependent.
	 * 
	 * @param another
	 *            a file to compare this file to
	 * @return an int determined by comparing the two paths. Possible values are
	 *         described in the Comparable interface.
	 * @see Comparable
	 */
	public int compareTo(File another)
	{
		return _name.compareTo(another._name);
	}
	
	/**
	 * Deletes this file. Directories must be empty before they will be deleted.
	 * 
	 * @return {@code true} if this file was deleted, {@code false} otherwise.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies the
	 *             request.
	 * @see SecurityManager#checkDelete
	 */
	public boolean delete()
	{
		if (anonymous())
			return true;
		
		if (exists())
		{
			_fileSystem.remove(_name);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Schedules this file to be automatically deleted once the virtual machine
	 * terminates. This will only happen when the virtual machine terminates
	 * normally as described by the Java Language Specification section 12.9.
	 * 
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies the
	 *             request.
	 */
	public void deleteOnExit()
	{
	}
	
	/**
	 * Compares {@code obj} to this file and returns {@code true} if they
	 * represent the <em>same</em> object using a path specific comparison.
	 * 
	 * @param obj
	 *            the object to compare this file with.
	 * @return {@code true} if {@code obj} is the same as this object,
	 *         {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof File))
			return false;

		File other = (File) obj;
		
		if (anonymous() || other.anonymous())
			return false;
		
		return _name.equals(other._name);
	}
	
	/**
	 * Returns a boolean indicating whether this file can be found on the
	 * underlying file system.
	 * 
	 * @return {@code true} if this file exists, {@code false} otherwise.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies read
	 *             access to this file.
	 * @see #getPath
	 */
	public boolean exists()
	{
		if (anonymous())
			return (_storage != null);
		else
			return _fileSystem.containsKey(_name);
	}
	
	private boolean anonymous()
	{
		return _name.isEmpty();
	}
	
	/**
	 * Returns the absolute path of this file.
	 * 
	 * @return the absolute file path.
	 * @see SecurityManager#checkPropertyAccess
	 */
	public String getAbsolutePath()
	{
		return _name;
	}
	
	/**
	 * Returns a new file constructed using the absolute path of this file.
	 * 
	 * @return a new file from this file's absolute path.
	 * @see SecurityManager#checkPropertyAccess
	 */
	public File getAbsoluteFile()
	{
		return this;
	}
	
	/**
	 * Returns the absolute path of this file with all references resolved. An
	 * <em>absolute</em> path is one that begins at the root of the file system.
	 * The canonical path is one in which all references have been resolved. For
	 * the cases of '..' and '.', where the file system supports parent and
	 * working directory respectively, these are removed and replaced with a
	 * direct directory reference. If the file does not exist,
	 * getCanonicalPath() may not resolve any references and simply returns an
	 * absolute path name or throws an IOException.
	 * 
	 * @return the canonical path of this file.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 * @see SecurityManager#checkPropertyAccess
	 */
	public String getCanonicalPath() throws IOException
	{
		return _name;
	}
		
	/**
	 * Returns a new file created using the canonical path of this file.
	 * Equivalent to {@code new File(this.getCanonicalPath())}.
	 * 
	 * @return the new file constructed from this file's canonical path.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 * @see SecurityManager#checkPropertyAccess
	 */
	public File getCanonicalFile() throws IOException
	{
		return this;
	}
	
	/**
	 * Returns the name of the file or directory represented by this file.
	 * 
	 * @return this file's name or an empty string if there is no name part in
	 *         the file's path.
	 */
	public String getName()
	{
		return _name;
	}
	
	/**
	 * Returns the pathname of the parent of this file. This is the path up to
	 * but not including the last name. {@code null} is returned if there is no
	 * parent.
	 * 
	 * @return this file's parent pathname or {@code null}.
	 */
	public String getParent()
	{
		return null;
	}
	
	/**
	 * Returns a new file made from the pathname of the parent of this file.
	 * This is the path up to but not including the last name. {@code null} is
	 * returned when there is no parent.
	 * 
	 * @return a new file representing this file's parent or {@code null}.
	 */
	public File getParentFile()
	{
		return null;
	}
	
	/**
	 * Returns the path of this file.
	 * 
	 * @return this file's path.
	 */
	public String getPath()
	{
		return _name;
	}
	
	/**
	 * Returns an integer hash code for the receiver. Any two objects for which
	 * {@code equals} returns {@code true} must return the same hash code.
	 * 
	 * @return this files's hash value.
	 * @see #equals
	 */
	@Override
	public int hashCode()
	{
		return _name.hashCode();
	}
	
	/**
	 * Indicates if this file's pathname is absolute. Whether a pathname is
	 * absolute is platform specific. On UNIX, absolute paths must start with
	 * the character '/'; on Windows it is absolute if either it starts with
	 * '\\' (to represent a file server), or a letter followed by a colon.
	 * 
	 * @return {@code true} if this file's pathname is absolute, {@code false}
	 *         otherwise.
	 * @see #getPath
	 */
	public boolean isAbsolute()
	{
		return true;
	}
	
	/**
	 * Indicates if this file represents a <em>directory</em> on the underlying
	 * file system.
	 * 
	 * @return {@code true} if this file is a directory, {@code false}
	 *         otherwise.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies read
	 *             access to this file.
	 */
	public boolean isDirectory()
	{
		return false;
	}
	
	/**
	 * Indicates if this file represents a <em>file</em> on the underlying file
	 * system.
	 * 
	 * @return {@code true} if this file is a file, {@code false} otherwise.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies read
	 *             access to this file.
	 */
	public boolean isFile()
	{
		return exists();
	}
	
	/**
	 * Returns whether or not this file is a hidden file as defined by the
	 * operating system. The notion of "hidden" is system-dependent. For Unix
	 * systems a file is considered hidden if its name starts with a ".". For
	 * Windows systems there is an explicit flag in the file system for this
	 * purpose.
	 * 
	 * @return {@code true} if the file is hidden, {@code false} otherwise.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies read
	 *             access to this file.
	 */
	public boolean isHidden()
	{
		if (anonymous())
			return true;
		
		return false;
	}
	
	/**
	 * Returns the time when this file was last modified, measured in
	 * milliseconds since January 1st, 1970, midnight.
	 * 
	 * @return the time when this file was last modified.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies read
	 *             access to this file.
	 */
	public long lastModified()
	{
		return 0;
	}
	
	/**
	 * Sets the time this file was last modified, measured in milliseconds since
	 * January 1st, 1970, midnight.
	 * 
	 * @param time
	 *            the last modification time for this file.
	 * @return {@code true} if the operation is successful, {@code false}
	 *         otherwise.
	 * @throws IllegalArgumentException
	 *             if {@code time < 0}.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies write
	 *             access to this file.
	 */
	public boolean setLastModified(long time)
	{
		return false;
	}
	
	/**
	 * Marks this file or directory to be read-only as defined by the operating
	 * system.
	 * 
	 * @return {@code true} if the operation is successful, {@code false}
	 *         otherwise.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies write
	 *             access to this file.
	 */
	public boolean setReadOnly()
	{
		return false;
	}
	
	/**
	 * Returns the length of this file in bytes.
	 * 
	 * @return the number of bytes in this file.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies read
	 *             access to this file.
	 */
	public long length()
	{
		if (anonymous() || exists())
		{
			ByteStore bs = getStorage();
			return _storage.length();
		}
		
		return 0;
	}
	
	/**
	 * Returns an array of strings with the file names in the directory
	 * represented by this file. The result is {@code null} if this file is not
	 * a directory.
	 * <p>
	 * The entries {@code .} and {@code ..} representing the current and parent
	 * directory are not returned as part of the list.
	 * 
	 * @return an array of strings with file names or {@code null}.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies read
	 *             access to this file.
	 * @see #isDirectory
	 */
	public String[] list()
	{
		return null;
	}
	
	/**
	 * Returns an array of files contained in the directory represented by this
	 * file. The result is {@code null} if this file is not a directory. The
	 * paths of the files in the array are absolute if the path of this file is
	 * absolute, they are relative otherwise.
	 * 
	 * @return an array of files or {@code null}.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies read
	 *             access to this file.
	 * @see #list
	 * @see #isDirectory
	 */
	public File[] listFiles()
	{
		return null;
	}
	
	/**
	 * Gets a list of the files in the directory represented by this file. This
	 * list is then filtered through a FilenameFilter and files with matching
	 * names are returned as an array of files. Returns {@code null} if this
	 * file is not a directory. If {@code filter} is {@code null} then all
	 * filenames match.
	 * <p>
	 * The entries {@code .} and {@code ..} representing the current and parent
	 * directories are not returned as part of the list.
	 * 
	 * @param filter
	 *            the filter to match names against, may be {@code null}.
	 * @return an array of files or {@code null}.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies read
	 *             access to this file.
	 * @see #list(cowj.java.io.FilenameFilter filter)
	 * @see #getPath
	 * @see #isDirectory
	 */
	public File[] listFiles(FilenameFilter filter)
	{
		return null;
	}
	
	/**
	 * Gets a list of the files in the directory represented by this file. This
	 * list is then filtered through a FileFilter and matching files are
	 * returned as an array of files. Returns {@code null} if this file is not a
	 * directory. If {@code filter} is {@code null} then all files match.
	 * <p>
	 * The entries {@code .} and {@code ..} representing the current and parent
	 * directories are not returned as part of the list.
	 * 
	 * @param filter
	 *            the filter to match names against, may be {@code null}.
	 * @return an array of files or {@code null}.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies read
	 *             access to this file.
	 * @see #getPath
	 * @see #isDirectory
	 */
	public File[] listFiles(FileFilter filter)
	{
		return null;
	}
	
	/**
	 * Gets a list of the files in the directory represented by this file. This
	 * list is then filtered through a FilenameFilter and the names of files
	 * with matching names are returned as an array of strings. Returns
	 * {@code null} if this file is not a directory. If {@code filter} is
	 * {@code null} then all filenames match.
	 * <p>
	 * The entries {@code .} and {@code ..} representing the current and parent
	 * directories are not returned as part of the list.
	 * 
	 * @param filter
	 *            the filter to match names against, may be {@code null}.
	 * @return an array of files or {@code null}.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies read
	 *             access to this file.
	 * @see #getPath
	 * @see #isDirectory
	 */
	public String[] list(FilenameFilter filter)
	{
		return null;
	}
	
	/**
	 * Creates the directory named by the trailing filename of this file. Does
	 * not create the complete path required to create this directory.
	 * 
	 * @return {@code true} if the directory has been created, {@code false}
	 *         otherwise.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies write
	 *             access for this file.
	 * @see #mkdirs
	 */
	public boolean mkdir()
	{
		return false;
	}
	
	/**
	 * Creates the directory named by the trailing filename of this file,
	 * including the complete directory path required to create this directory.
	 * 
	 * @return {@code true} if the necessary directories have been created,
	 *         {@code false} if the target directory already exists or one of
	 *         the directories can not be created.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies write
	 *             access for this file.
	 * @see #mkdir
	 */
	public boolean mkdirs()
	{
		return false;
	}
	
	/**
	 * Creates a new, empty file on the file system according to the path
	 * information stored in this file.
	 * 
	 * @return {@code true} if the file has been created, {@code false} if it
	 *         already exists.
	 * @throws java.io.IOException
	 *             if an I/O error occurs or the directory does not exist where
	 *             the file should have been created.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies write
	 *             access for this file.
	 */
	public boolean createNewFile() throws IOException
	{
		if (exists())
			return false;
		
		_storage = new ByteStore();
		if (!anonymous())
			_fileSystem.put(_name, _storage);
		
		return true;
	}
	
	/**
	 * Creates an empty temporary file using the given prefix and suffix as part
	 * of the file name. If suffix is {@code null}, {@code .tmp} is used. This
	 * method is a convenience method that calls
	 * {@link #createTempFile(String, String, cowj.java.io.File)} with the third argument
	 * being {@code null}.
	 * 
	 * @param prefix
	 *            the prefix to the temp file name.
	 * @param suffix
	 *            the suffix to the temp file name.
	 * @return the temporary file.
	 * @throws java.io.IOException
	 *             if an error occurs when writing the file.
	 */
	public static File createTempFile(String prefix, String suffix)
	        throws IOException
	{
		return createTempFile(prefix, suffix, null);
	}
	
	/**
	 * Creates an empty temporary file in the given directory using the given
	 * prefix and suffix as part of the file name.
	 * 
	 * @param prefix
	 *            the prefix to the temp file name.
	 * @param suffix
	 *            the suffix to the temp file name.
	 * @param directory
	 *            the location to which the temp file is to be written, or
	 *            {@code null} for the default location for temporary files,
	 *            which is taken from the "java.io.tmpdir" system property. It
	 *            may be necessary to set this property to an existing, writable
	 *            directory for this method to work properly.
	 * @return the temporary file.
	 * @throws IllegalArgumentException
	 *             if the length of {@code prefix} is less than 3.
	 * @throws java.io.IOException
	 *             if an error occurs when writing the file.
	 */
	@SuppressWarnings("nls")
	public static File createTempFile(String prefix, String suffix,
	        File directory) throws IOException
	{
		if (directory != null)
			throw new UnsupportedOperationException("createTempFile() directory must be null");
		
		int i;
		for (;;)
		{
			i = (int)(Math.random() * 0x10000);
			String name = prefix + ".temp." + i + "." + suffix;
			if (_fileSystem.containsKey(name))
				return new File(name);
		}
	}
	
	/**
	 * Renames this file to the name represented by the {@code dest} file. This
	 * works for both normal files and directories.
	 * 
	 * @param dest
	 *            the file containing the new name.
	 * @return {@code true} if the File was renamed, {@code false} otherwise.
	 * @throws SecurityException
	 *             if a {@code SecurityManager} is installed and it denies write
	 *             access for this file or the {@code dest} file.
	 */
	public boolean renameTo(File dest)
	{
		if (!exists())
			return false;
		
		if (dest.exists())
		{
			if (!dest.delete())
				return false;
		}

		if (!dest._name.isEmpty())
			_fileSystem.put(dest._name, _storage);
		_fileSystem.remove(_name);
		_name = dest._name;
		dest._storage = _storage;
		return true;
	}
	
	/**
	 * Returns a string containing a concise, human-readable description of this
	 * file.
	 * 
	 * @return a printable representation of this file.
	 */
	@Override
	public String toString()
	{
		return _name;
	}
	
	ByteStore getStorage()
	{
		if (_storage != null)
			return _storage;
		
		_storage = _fileSystem.get(_name);
		return _storage;
	}
	
	FileChannel createChannel() throws FileNotFoundException
	{
		return new FileChannelImpl(this);
	}
}
