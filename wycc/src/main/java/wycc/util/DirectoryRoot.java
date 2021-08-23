package wycc.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import wycc.lang.Content;
import wycc.lang.Content.Root;
import wycc.lang.Content.Type;

/**
 * Provides an implementation of <code>Content.Source</code>
 * <code>Content.Sink</code> for representing a file system directory.
 *
 * @author David J. Pearce
 *
 */
public class DirectoryRoot implements Content.Root, Iterable<Content> {
	public final static FileFilter NULL_FILTER = new FileFilter() {
		@Override
		public boolean accept(File file) {
			return true;
		}
	};
    private final Content.Registry registry;
    private final File dir;
    private final ArrayList<Entry<?>> items;

    public DirectoryRoot(Content.Registry registry, File dir) throws IOException {
        this(registry, dir, NULL_FILTER);
    }

    public DirectoryRoot(Content.Registry registry, File dir, FileFilter filter) throws IOException {
		this.registry = registry;
		this.dir = dir;
		this.items = initialise(registry, dir, filter);
    }

    /**
	 * Get the content registry associated with this root.
	 *
	 * @return
	 */
    @Override
    public Content.Registry getContentRegistry() {
    	return registry;
    }

	@SuppressWarnings("unchecked")
	@Override
	public <S extends Content> S get(Content.Type<S> kind, Trie p) {
		for (int i = 0; i != items.size(); ++i) {
			Entry<?> ith = items.get(i);
			Content.Type<?> ct = ith.getContentType();
			if (ith.getPath().equals(p) && ct == kind) {
				return (S) ith.get();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S extends Content> List<S> getAll(Content.Filter<S> filter) {
		ArrayList<S> rs = new ArrayList<>();
		for (int i = 0; i != items.size(); ++i) {
			Entry<?> ith = items.get(i);
			if (filter.includes(ith.getContentType(), ith.getPath())) {
				rs.add((S) ith.get());
			}
		}
		return rs;
	}

	@Override
	public List<Trie> match(Content.Filter<?> filter) {
		ArrayList<Trie> rs = new ArrayList<>();
		for (int i = 0; i != items.size(); ++i) {
			Entry<?> ith = items.get(i);
			if (filter.includes(ith.getContentType(), ith.getPath())) {
				rs.add(ith.getPath());
			}
		}
		return rs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S extends Content> List<Trie> match(Content.Filter<S> filter, Predicate<S> f) {
		ArrayList<Trie> rs = new ArrayList<>();
		for (int i = 0; i != items.size(); ++i) {
			Entry<?> ith = items.get(i);
			Content.Type<?> ct = ith.getContentType();
			if (filter.includes(ct, ith.getPath())) {
				S item = (S) ith.get();
				if (f.test(item)) {
					rs.add(ith.getPath());
				}
			}
		}
		return rs;
	}

	@Override
	public Content.Root subroot(Trie path) {
		return new SubRoot(path);
	}

	@Override
	public void synchronise() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Content> iterator() {
		// Add wrapping iterator which forces loading of artifacts.
		return new Iterator<Content>() {
			int index = 0;
			@Override
			public boolean hasNext() {
				return index < items.size();
			}

			@Override
			public Content next() {
				return items.get(index++).get();
			}

		};
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void put(Trie p, Content value) {
		// NOTE: yes, there is unsafe stuff going on here because we cannot easily type
		// this in Java.
		Content.Type ct = (Content.Type) value.getContentType();
		// Update state
		for (int i = 0; i != items.size(); ++i) {
			Entry ith = items.get(i);
			if (ith.getContentType() == ct && ith.getPath().equals(p)) {
				// Yes, overwrite existing entry
				ith.set(value);
				return;
			}
		}
		Entry<Content> e = new Entry<>(p, ct);
		e.set(value);
		// Create new entry
		items.add(e);
	}

    /**
	 * Flush all dirty files to disk. A dirty file is one which has been modified
	 * since this content root was initialised.
	 */
	@Override
	public void flush() throws IOException {
		for (int i = 0; i != items.size(); ++i) {
			items.get(i).flush();
		}
	}

    /**
     * Get the root directory where this repository starts from.
     *
     * @return
     */
    public File getDirectory() {
        return dir;
    }

    @Override
	public String toString() {
    	String r = "{";
    	boolean firstTime = true;
		for (Entry<?> f : items) {
			if (!firstTime) {
				r += ",";
			}
			r += f.getPath();
			firstTime = false;
		}
    	return r + "}";
    }

    /**
	 * Construct the initial listing of files from the contents of the build
	 * directory. Observe that this does not load the files, but rather returns a
	 * list of "place holders".
	 *
	 * @param registry
	 * @param dir
	 * @param filter
	 * @return
	 * @throws IOException
	 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private ArrayList<Entry<?>> initialise(Content.Registry registry, File dir, FileFilter filter) throws IOException {
		java.nio.file.Path root = dir.toPath();
		// First extract all files rooted in this directory
		List<File> files = findAll(64, dir, filter, new ArrayList<>());
		// Second convert them all into entries as appropriate
		ArrayList<Entry<?>> entries = new ArrayList<>();
		//
		for (int i = 0; i != files.size(); ++i) {
			File ith = files.get(i);
			String filename = root.relativize(ith.toPath()).toString().replace(File.separatorChar, '/');
			// Decode filename into path and content type.
			Pair<Trie,Content.Type<?>> p = decodeFilename(filename, registry);
			if(p != null) {
				// Decoding was successfull!
				Content.Type ct = (Content.Type) p.second();
				// Create lazy artifact
				entries.add(new Entry(p.first(), ct));
			}
		}
		// Done
		return entries;
    }

    /**
	 * An entry within this root which corresponds (in theory) to an entry on disk.
	 * The content of an entry is loaded lazily on demand since, in general, this
	 * may involve a complex operation (e.g. decoding a structured file). An entry
	 * may not actually correspond to anything on disk if it has been created during
	 * execution, but not yet flushed to disk.
	 *
	 * @author David J. Pearce
	 *
	 * @param <S>
	 */
	private class Entry<S extends Content> {
		/**
		 * The repository path to which this entry corresponds.
		 */
		private final Trie path;
		/**
		 * The content type of this entry
		 */
		private final Content.Type<S> contentType;
		/**
		 * Indicates whether this entry has been modified or not.
		 */
		private boolean dirty;
		/**
		 * The cached value of this entry. This may be <code>null</code> if the entry
		 * has been read from disk yet.
		 */
		private S value;

		public Entry(Trie path, Content.Type<S> contentType) {
			this.path = path;
			this.contentType = contentType;
			this.dirty = false;
		}

		public Trie getPath() {
			return path;
		}

		public Content.Type<S> getContentType() {
			return contentType;
		}

		public S get() {
			try {
				if (value == null) {
					File f = getFile();
					FileInputStream fin = new FileInputStream(f);
					value = contentType.read(path, fin, DirectoryRoot.this.registry);
					fin.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return value;
		}

		public void set(S value) {
			if(this.value != value) {
				this.dirty = true;
				this.value = value;
			}
		}

		public void flush() throws IOException {
			// Only flush if the entry is actually dirty
			if (dirty) {
				File f = getFile();
				if (!f.exists()) {
					// Create any enclosing directories as necessary.
					f.getParentFile().mkdirs();
					// Attempt to create the file.
					if (!f.createNewFile()) {
						// Error creating file occurred
						return;
					}
				}
				// File now exists, therefore we can write to it.
				FileOutputStream fout = new FileOutputStream(f);
				contentType.write(fout, value);
				fout.close();
			}
		}

		private File getFile() {
	        String filename = path.toString().replace("/", File.separator) + "." + contentType.getSuffix();
	        // Done.
	        return new File(dir, filename);
		}
	}

	/**
	 * Used to extracting a root which is relative to another root.
	 *
	 * @author David J. Pearce
	 *
	 */
	private class SubRoot implements Content.Root {
		private final Trie path;

		SubRoot(Trie path) {
			this.path = path;
		}

		@Override
		public Content.Registry getContentRegistry() {
			return registry;
		}

		@Override
		public <T extends Content> T get(Type<T> ct, Trie p) throws IOException {
			return DirectoryRoot.this.get(ct, path.append(p));
		}

		@Override
		public <T extends Content> List<T> getAll(Content.Filter<T> filter) throws IOException {
			Content.Filter<T> f = new Content.Filter<>() {
				@Override
				public boolean includes(Type<?> ct, Trie p) {
					return filter.includes(ct, path.append(p));
				}
			};
			return DirectoryRoot.this.getAll(f);
		}

		@Override
		public List<Trie> match(Content.Filter<?> filter) {
			Content.Filter<?> f = new Content.Filter<>() {
				@Override
				public boolean includes(Type<?> ct, Trie p) {
					return filter.includes(ct, path.append(p));
				}
			};
			return DirectoryRoot.this.match(f);
		}

		@Override
		public <T extends Content> List<Trie> match(Content.Filter<T> cf, Predicate<T> p) {
			throw new UnsupportedOperationException("implement me");
		}

		@Override
		public void put(Trie p, Content value) {
			DirectoryRoot.this.put(path.append(p), value);
		}

		@Override
		public Root subroot(Trie p) {
			return DirectoryRoot.this.subroot(path.append(p));
		}

		@Override
		public void synchronise() throws IOException {
			DirectoryRoot.this.synchronise();
		}

		@Override
		public void flush() throws IOException {
			DirectoryRoot.this.flush();
		}
	}

    /**
	 * Decode a given filename into a path and an appropriate content type based on
	 * the filename's suffix. If no content type is found, or the path is malformed
	 * then <code>null</code> is returned.
	 *
	 * @param filename The filename to be decoded (e.g. <code>main.whiley</code>)
	 * @param registry The registry which associates suffixes with
	 *                 <code>Content.Type</code>s.
	 * @return
	 */
	private static Pair<Trie, Content.Type<?>> decodeFilename(String filename, Content.Registry registry) {
		// Determine file suffix
		String suffix = "";
		int pos = filename.lastIndexOf('.');
		if (pos > 0) {
			suffix = filename.substring(pos + 1);
		}
		// Extract the path string
		String pathStr = filename.substring(0, filename.length() - (suffix.length() + 1));
		// Convert into path (if applicable)
		Trie path = Trie.fromString(pathStr);
		// Extract appropriate content type (if applicable)
		Content.Type<?> type = registry.contentType(suffix);
		// Read entry (if applicable)
		if (type != null) {
			// Done
			return new Pair<>(path, type);
		}
		return null;
	}

    /**
     * Extract all files starting from a given directory.
     *
     * @param dir
     * @return
     */
    private static List<File> findAll(int n, File dir, FileFilter filter, List<File> files) {
        if (n > 0 && dir.exists() && dir.isDirectory()) {
            File[] contents = dir.listFiles(filter);
            for (int i = 0; i != contents.length; ++i) {
                File ith = contents[i];
                //
                if (ith.isDirectory()) {
                    findAll(n - 1, ith, filter, files);
                } else {
                    files.add(ith);
                }
            }
        }
        return files;
    }
}
