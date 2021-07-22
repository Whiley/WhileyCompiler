package wycc.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import wycc.lang.Build;
import wycc.lang.Content;
import wycc.lang.Content.Registry;
import wycc.lang.Content.Type;
import wycc.lang.Filter;
import wycc.lang.Path;

public class ByteRepository implements Build.Repository {
	private final Content.Registry registry;
	private final ArrayList<SnapShot> states;

	public ByteRepository(Content.Registry registry, Iterable<Build.Artifact> entries) {
		this(registry, entries.iterator());
	}

	public ByteRepository(Content.Registry registry, Iterator<Build.Artifact> entries) {
		this.registry = registry;
		this.states = new ArrayList();
		states.add(new SnapShot(entries));
	}

	public ByteRepository(Content.Registry registry, Build.Artifact... entries) {
		this.registry = registry;
		this.states = new ArrayList<>();
		states.add(new SnapShot(entries));
	}

	@Override
	public int size() {
		return states.size();
	}

	@Override
	public Registry getContentRegistry() {
		return registry;
	}

	@Override
	public SnapShot get(int i) {
		return states.get(i);
	}

	@Override
	public <T extends Content> T get(Content.Type<T> kind, Path path) {
		return last().get(kind, path);
	}

	@Override
	public <T extends Content> List<T> getAll(Content.Type<T> kind, Filter filter) {
		return last().getAll(kind, filter);
	}

	@Override
	public List<Path> match(Content.Type<? extends Content> ct, Filter filter) {
		return last().match(ct, filter);
	}

	@Override
	public <S extends Content> List<Path> match(Content.Type<S> kind, Predicate<S> p) {
		return last().match(kind,p);
	}

	@Override
	public SnapShot last() {
		return states.get(states.size() - 1);
	}

	@Override
	public void apply(Function<Build.SnapShot, Build.SnapShot> transformer) {
		states.add((SnapShot) transformer.apply(last()));
	}

	@Override
	public String toString() {
		String r = "";
		for(SnapShot s : states) {
			r += s.toString();
		}
		return r;
	}

	public class SnapShot implements Build.SnapShot, Iterable<Build.Artifact> {
		private final ArrayList<Build.Artifact> items = new ArrayList<>();

		public SnapShot(Iterator<Build.Artifact> entries) {
			while(entries.hasNext()) {
				items.add(entries.next());
			}
		}

		public SnapShot(Build.Artifact... entries) {
			for (int i = 0; i != entries.length; ++i) {
				items.add(entries[i]);
			}
		}

		private SnapShot(SnapShot s) {
			this.items.addAll(s.items);
		}

		@Override
		public Registry getContentRegistry() {
			return ByteRepository.this.registry;
		}

		@Override
		public <T extends Content> List<Path> match(Content.Type<T> kind, Predicate<T> query) {
			ArrayList<Path> es = new ArrayList<>();
			for (Build.Artifact e : items) {
				if (e.getContentType() == kind && query.test((T) e)) {
					es.add(e.getPath());
				}
			}
			return es;
		}

		@Override
		public List<Path> match(Content.Type<? extends Content> ct, Filter filter) {
			ArrayList<Path> es = new ArrayList<>();
			for (Build.Artifact e : items) {
				if (e.getContentType() == ct && filter.matches(e.getPath())) {
					es.add(e.getPath());
				}
			}
			return es;
		}

		@Override
		public <T extends Content> T get(Content.Type<T> kind, Path path) {
			for (Build.Artifact e : items) {
				if (e.getContentType() == kind && e.getPath().equals(path)) {
					return (T) e;
				}
			}
			return null;
		}

		@Override
		public <T extends Content> List<T> getAll(Content.Type<T> ct, Filter filter) {
			ArrayList<T> es = new ArrayList<>();
			for (Build.Artifact e : items) {
				if (e.getContentType() == ct && filter.matches(e.getPath())) {
					es.add((T) e);
				}
			}
			return es;
		}

		@Override
		public SnapShot put(Build.Artifact entry) {
			// Clone state
			SnapShot s = new SnapShot(this);
			// Update state
			for (int i = 0; i != items.size(); ++i) {
				Build.Artifact e = items.get(i);
				if (e.getClass() == entry.getClass() && e.getPath().equals(entry.getPath())) {
					// Overwrite existing entry
					s.items.set(i, entry);
					return s;
				}
			}
			// Create new entry
			s.items.add(entry);
			// Done
			return s;
		}

		@Override
		public Iterator<Build.Artifact> iterator() {
			return items.iterator();
		}

		@Override
		public String toString() {
			String r = "{";
			boolean firstTime=true;
			for(Build.Artifact f : items) {
				r = firstTime ? r : r + ",";
				r += f.getPath();
				firstTime=false;
			}
			return r + "}";
		}
	}
}
