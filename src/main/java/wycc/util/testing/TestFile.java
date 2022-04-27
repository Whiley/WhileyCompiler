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
package wycc.util.testing;

import java.io.*;
import java.util.*;

import wycc.util.TextFile;
import wycc.util.Trie;

/**
 * Represents a test-file as described in the <code>test_file_format</code> RFC.
 *
 * @author David J. Pearce
 *
 */
public class TestFile implements Iterable<TestFile.Frame> {
	private final HashMap<String,Object> config;
	private final Frame[] frames;

	public TestFile(Map<String, Object> config, List<Frame> frames) {
		this.config = new HashMap<>(config);
		this.frames = frames.toArray(new Frame[frames.size()]);
	}

	public <T> Optional<T> get(Class<T> kind, String option) {
		Object o = config.get(option);
		if(kind.isInstance(o)) {
			return Optional.of((T) o);
		} else {
			return Optional.empty();
		}
	}

	public Set<String> keys() {
		return config.keySet();
	}

	@Override
	public Iterator<Frame> iterator() {
		return Arrays.stream(frames).iterator();
	}

	/**
	 * Represents a frame within a testfile.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Frame {
		public final Action[] actions;
		public final Error[] markers;

		public Frame(List<Action> actions, List<Error> markers) {
			this.actions = actions.toArray(new Action[actions.size()]);
			this.markers = markers.toArray(new Error[markers.size()]);
		}

		public void apply(Map<Trie,TextFile> state) {
			for(Action a : actions) {
				a.apply(state);
			}
		}

		@Override
		public String toString() {
			String r = "";
			for(Action a : actions) {
				r += a.toString();
			}
			for(Error e : markers) {
				r += e.toString();
			}
			return r;
		}
	}

	/**
	 * Identifies an expected error at a location in a given source file.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Error implements Comparable<Error> {
		final int errno;
		final Trie filename;
		final Coordinate location;

		public Error(int errno, Trie filename, Coordinate loc) {
			this.errno = errno;
			this.filename = filename;
			this.location = loc;
		}

		@Override
		public String toString() {
			return "(E" + errno + "," + filename + "," + location + ")";
		}

		@Override
		public int compareTo(Error other) {
			int c = filename.compareTo(other.filename);
			if (c == 0) {
				c = location.compareTo(other.location);
				if (c == 0) {
					c = Integer.compare(errno, other.errno);
				}
			}
			return c;
		}
	}

	/**
	 * Identifies a span within a specific source file, which the error covers.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Coordinate implements Comparable<Coordinate> {
		public final int line;
		public final Range range;

		public Coordinate(int line, Range range) {
			this.line = line;
			this.range = range;
		}

		@Override
		public String toString() {
			return line + "," + range;
		}

		@Override
		public int compareTo(Coordinate other) {
			int c = Integer.compare(this.line, other.line);
			if(c == 0) {
				return range.compareTo(other.range);
			}
			return c;
		}
	}

	/**
	 * Represents an interval (e.g. of characters within a line).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Range implements Comparable<Range> {
		public final int start;
		public final int end;

		public Range(int start, int end) {
			this.start = start;
			this.end = end;
		}

		@Override
		public String toString() {
			if(start == end) {
				return Integer.toString(start);
			} else {
				return start + ":" + end;
			}
		}

		@Override
		public int compareTo(Range other) {
			int c = Integer.compare(start, other.start);
			if(c == 0) {
				return Integer.compare(end, other.end);
			}
			return c;
		}
	}

	/**
	 * Abstracts the various actions which can occur wihin a frame, such as writing
	 * to a file.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Action {
		public enum Kind {
			INSERT,
			REMOVE
		}
		//
		public final Kind kind;
		public final Trie filename;
		public final Range range;
		public final String[] lines;

		public Action(Kind kind, Trie filename, Range range, List<String> lines) {
			this.kind = kind;
			this.filename = filename;
			this.range = range;
			this.lines = lines.toArray(new String[lines.size()]);
		}

		public void apply(Map<Trie,TextFile> state) {
			if (kind == Kind.REMOVE && state.containsKey(filename)) {
				// Easy case
				state.remove(filename);
			} else if (kind == Kind.REMOVE) {
				throw new IllegalArgumentException("Invalid remove action");
			} else if(state.containsKey(filename)) {
				TextFile tf = state.get(filename);
				state.put(filename, tf.replace(range.start, range.end, lines));
			} else if(range == null){
				// Create entirely new file.
				state.put(filename, new TextFile(lines));
			} else {
				throw new IllegalArgumentException("Invalid insert action");
			}
		}

		@Override
		public String toString() {
			return "(" + kind + "," + filename + "," + range + "," + Arrays.toString(lines) + ")";
		}
	}

	public static TestFile parse(InputStream in) throws IOException {
		return parse(new InputStreamReader(in));
	}

	public static TestFile parse(Reader in) throws IOException {
		HashMap<String,Object> config = new HashMap<>();
		ArrayList<Frame> frames = new ArrayList<>();
		// Split file into lines
		List<String> lines = parseLines(in);
		// Read initial configuration
		int i = parseConfig(lines,config);
		// Read frames
		while(i < lines.size()) {
			i = parseFrame(i,lines,frames);
		}
		// Done
		return new TestFile(config,frames);
	}

	private static List<String> parseLines(Reader in) throws IOException {
		ArrayList<String> lines = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(in)) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	lines.add(line);
		    }
		}
		return lines;
	}

	private static int parseConfig(List<String> lines, Map<String,Object> config) {
		int i = 0;
		while(i < lines.size()) {
			String ith = lines.get(i);
			if(ith.startsWith("===")) {
				return i + 1;
			} else {
				String[] split = ith.split("=");
				String key = split[0];
				String val = split[1];
				if(config.containsKey(key)) {
					throw new IllegalArgumentException("duplicate key encountered (\"" + key + "\")");
				} if(val.charAt(0) == '"') {
					// Strip quotes
					config.put(key, val.substring(1, val.length() - 1));
				} else if(Character.isDigit(val.charAt(0))) {
					config.put(key, Integer.parseInt(val));
				} else {
					config.put(key, Boolean.parseBoolean(val));
				}
			}
			i = i + 1;
		}
		return i;
	}

	private static int parseFrame(int i, List<String> lines, List<Frame> frames) {
		ArrayList<Action> actions = new ArrayList<>();
		while(i < lines.size()) {
			String ith = lines.get(i);
			if(ith.startsWith("===")) {
				// No error markers in this frame
				frames.add(new Frame(actions,Collections.emptyList()));
				return i+1;
			} else if(ith.startsWith("---")) {
				i = i + 1;
				break;
			}
			i = parseAction(i,lines,actions);
		}
		ArrayList<Error> errors = new ArrayList<>();
		while(i < lines.size()) {
			String ith = lines.get(i);
			if(ith.startsWith("===")) {
				i = i + 1;
				break;
			}
			errors.add(parseError(lines.get(i++)));
		}
		frames.add(new Frame(actions,errors));
		return i;
	}

	private static int parseAction(int i, List<String> lines, List<Action> actions) {
		String ith = lines.get(i);
		String[] split = ith.split(" ");
		Action.Kind kind = parseKind(split[0]);
		Trie filename = Trie.fromString(split[1]);
		Range range = split.length > 2 ? parseRange(split[2]) : null;
		ArrayList<String> text = new ArrayList<>();
		while (++i < lines.size()) {
			ith = lines.get(i);
			if (ith.startsWith("---") || ith.startsWith("<<<") || ith.startsWith(">>>") || ith.startsWith("===")) {
				break;
			}
			text.add(ith);
		}
		actions.add(new Action(kind, filename, range, text));
		return i;
	}

	private static Action.Kind parseKind(String line) {
		if(line.equals(">>>")) {
			return Action.Kind.INSERT;
		} else if(line.equals("<<<")) {
			return Action.Kind.REMOVE;
		} else {
			throw new IllegalArgumentException("invalid file");
		}
	}

	private static Error parseError(String line) {
		String[] split = line.split(" ");
		int errno = Integer.parseInt(split[0].substring(1));
		Trie filename = Trie.fromString(split[1]);
		Coordinate coord = parseCoordinate(split[2]);
		return new Error(errno,filename,coord);
	}

	private static Coordinate parseCoordinate(String line) {
		String[] split = line.split(",");
		int l = Integer.parseInt(split[0]);
		Range range = parseRange(split[1]);
		return new Coordinate(l,range);
	}

	private static Range parseRange(String text) {
		String[] split = text.split(":");
		int start = Integer.parseInt(split[0]);
		if(split.length == 1) {
			return new Range(start,start);
		} else {
			int end = Integer.parseInt(split[1]);
			return new Range(start,end);
		}
	}

	public static void main(String[] args) throws IOException {
		FileInputStream fin = new FileInputStream("test.wytest");
		TestFile tf = parse(fin);
		for(String key : tf.keys()) {
			System.out.println(key + " = " + tf.get(Object.class, key).get());
		}
		HashMap<Trie,TextFile> state = new HashMap<>();
		for(TestFile.Frame f : tf) {
			System.out.println(">" + f);
			f.apply(state);
			System.out.println("STATE: " + state.toString());
		}
	}
}
