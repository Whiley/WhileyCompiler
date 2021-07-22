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
package wycli.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wycli.cfg.Configuration;
import wycli.lang.Command;
import wycli.lang.Package;
import wycli.lang.Semantic;
import wycc.lang.Content;
import wycc.lang.Filter;
import wycc.lang.Path;
import wycc.util.ZipFile;

/**
 *
 * @author djp
 *
 */
public class LocalPackageRepository implements Package.Repository {
	public static final Path REPOSITORY_DIR = Path.fromStrings("repository", "dir");

	/**
	 * Schema for global configuration (i.e. which applies to all projects for a given user).
	 */
	public static Configuration.Schema SCHEMA = Configuration
			.fromArray(Configuration.UNBOUND_STRING(REPOSITORY_DIR, "local directory", false));

	protected final Command.Environment environment;
	protected final Package.Repository parent;
	protected final Content.Root root;

	public LocalPackageRepository(Command.Environment environment, Content.Root root) throws IOException {
		this(environment,null,root);
	}

	public LocalPackageRepository(Command.Environment environment, Package.Repository parent,
			Content.Root root)
			throws IOException {
		this.parent = parent;
		this.environment = environment;
		this.root = root;
	}

	@Override
	public Package.Repository getParent() {
		return parent;
	}

	@Override
	public Set<Semantic.Version> list(String pkg) throws IOException {
		List<Path> matches = root.match(ZipFile.ContentType, Filter.EVERYTHING);
		HashSet<Semantic.Version> versions = new HashSet<>();
		String prefix = pkg + "-v";
		for(Path m : matches) {
			// FIXME: need for m.last() seems like bug
			String str = m.last().toString();
			if(str.startsWith(prefix)) {
				Semantic.Version v = new Semantic.Version(str.substring(prefix.length()));
				versions.add(v);
			}
		}
		return versions;
	}

	@Override
	public ZipFile get(String pkg, Semantic.Version version) throws IOException {
		Path id = Path.fromString(pkg + "-v" + version);
		ZipFile zf = root.get(ZipFile.ContentType, id);
		// Attempt to resolve it.
		if (zf == null) {
			environment.getLogger().logTimedMessage("Failed loading  " + pkg + "-v" + version, 0, 0);
			return null;
		} else {
			// Extract entry for ZipFile
			return zf;
		}
	}

	@Override
	public void put(ZipFile pkg, String name, Semantic.Version version) throws IOException {
		// Determine fully qualified package name
		Path qpn = Path.fromString(name + "-v" + version);
		// Dig out the file!
		root.put(qpn, pkg);
		//
		root.flush();
		//
		environment.getLogger().logTimedMessage("Installed " + qpn, 0, 0);
	}

}
