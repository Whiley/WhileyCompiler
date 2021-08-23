// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wycc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import wycc.lang.Content;

/**
 * A shim for handling ZipFiles in a uniform fashion within the Whiley File
 * System (WyFS).
 *
 * @author David J. Pearce
 *
 */
public class ZipFile implements Content, Content.Source {

	public static Content.Type<ZipFile> ContentType = new Content.Type<ZipFile>() {
		@Override
		public String getSuffix() {
			return "zip";
		}

		@Override
		public ZipFile read(Trie id, InputStream input, Content.Registry registry) throws IOException {
			return new ZipFile(registry, input);
		}

		@Override
		public void write(OutputStream output, ZipFile zf) throws IOException {
			ZipOutputStream zout = new ZipOutputStream(output);
			for (int i = 0; i != zf.size(); ++i) {
				Entry<?> e = zf.get(i);
				// Create filename
				String filename = e.path.toString() + "." + e.contentType.getSuffix();
				zout.putNextEntry(new ZipEntry(filename));
				zout.write(e.bytes);
				zout.closeEntry();
			}
			zout.finish();
		}
	};

	/**
	 * The content registry used by this ZipFile for interpreting content.
	 */
	private final Content.Registry registry;

	/**
	 * Contains the list of entries in the zip file.
	 */
	private final List<Entry<? extends Content>> entries;

	/**
	 * Construct an empty ZipFile
	 */
	public ZipFile(Content.Registry registry) {
		this.registry = registry;
		this.entries = new ArrayList<>();
	}

	/**
	 * Construct a ZipFile from a given input stream representing a zip file.
	 *
	 * @param input
	 */
	public ZipFile(Content.Registry registry, InputStream input) throws IOException {
		this.registry = registry;
		this.entries = new ArrayList<>();
		// Read all entries from the input stream
		ZipInputStream zin = new ZipInputStream(input);
		ZipEntry e;
		while ((e = zin.getNextEntry()) != null) {
			byte[] contents = readEntryContents(zin);
			// FIXME: making certain assumptions here about what's in the Zip file. For
			// example, a filename with no suffix will cause a crash here.
			String[] ns = e.getName().split("\\.");
			if(ns.length > 1) {
				Trie p = Trie.fromString(ns[0]);
				Content.Type<?> ct = registry.contentType(ns[1]);
				entries.add(new Entry<>(ct, p, contents));
			}
			zin.closeEntry();
		}
		zin.close();
	}

	public int size() {
		return entries.size();
	}

	@Override
	public Content.Type<?> getContentType() {
		return ContentType;
	}

	@Override
	public Content.Registry getContentRegistry() {
		return registry;
	}

//	public void add(ZipEntry entry, byte[] bytes) {
//		this.entries.add(new Entry(entry,bytes));
//	}

	/**
	 * Get the ith entry in this ZipFile.
	 *
	 * @param i
	 * @return
	 */
	public Entry<?> get(int i) {
		return entries.get(i);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Content> T get(Content.Type<T> kind, Trie p) {
		for (int i = 0; i != entries.size(); ++i) {
			Entry<?> ith = entries.get(i);
			if (ith.getTrie().equals(p) && ith.getContentType() == kind) {
				return (T) ith.get();
			}
		}
		// Didn't find anything.
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Content> List<T> getAll(Content.Filter<T> filter) {
		ArrayList<T> rs = new ArrayList<>();
		for (int i = 0; i != entries.size(); ++i) {
			Entry<?> ith = entries.get(i);
			if (filter.includes(ith.getContentType(),ith.getTrie())) {
				rs.add((T) ith.get());
			}
		}
		return rs;
	}

	@Override
	public List<Trie> match(Content.Filter<? extends Content> filter) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends Content> List<Trie> match(Content.Filter<T> ct, Predicate<T> p) {
		throw new UnsupportedOperationException();
	}

	private byte[] readEntryContents(InputStream in) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		// Read bytes in max 1024 chunks
		byte[] data = new byte[1024];
		// Read all bytes from the input stream
		while ((nRead = in.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		// Done
		buffer.flush();
		return buffer.toByteArray();
	}

	public final class Entry<T extends Content> {
		public final Trie path;
		public final Content.Type<T> contentType;
		public final byte[] bytes;
		public T value;

		public Entry(Content.Type<T> contentType, Trie path, byte[] bytes) {
			this.contentType = contentType;
			this.path = path;
			this.bytes = bytes;
		}

		public Trie getTrie() {
			return path;
		}

		public Content.Type<?> getContentType() {
			return contentType;
		}

		public T get() {
			try {
				if(value == null) {
					value = contentType.read(path, getInputStream(), ZipFile.this.registry);
				}
				return value;
			} catch (IOException e) {
				return null;
			}
		}

		public InputStream getInputStream() {
			return new ByteArrayInputStream(bytes);
		}
	}
}
