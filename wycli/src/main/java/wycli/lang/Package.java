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
package wycli.lang;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import wycc.lang.Build;
import wycc.lang.Content;
import wycc.util.ZipFile;
import wycli.cfg.Configuration;

public interface Package {

	/**
	 * Responsible for resolving version strings into concrete packages.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Resolver {
		/**
		 * Resolve a given package name and version string, which may include additional
		 * modifiers (e.g. semantic versioning constraints).
		 *
		 * @param pkg
		 * @param version
		 * @return
		 */
		List<Content.Source> resolve(Configuration cf) throws IOException;

		/**
		 * Get the root repository associated with this package resolver.
		 *
		 * @return
		 */
		Repository getRepository();
	}

	/**
	 * Represents a store of packages, such as on disk or in the cloud.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Repository {
		/**
		 * Get the parent repository for this repository (or <code>null</code> if this
		 * is the root repository).
		 *
		 * @return
		 */
		public Package.Repository getParent();

		/**
		 * List all known versions of a given package. This is used for resolution,
		 * amongst other things.
		 *
		 * @param pkg
		 * @return
		 */
		public Set<Semantic.Version> list(String pkg) throws IOException;

		/**
		 * Get a given package in this repository. If no such package exists, an
		 * <code>IllegalArgumentException</code> is thrown.
		 *
		 * @param name
		 * @param version
		 * @return
		 */
		public ZipFile get(String name, Semantic.Version version) throws IOException;

		/**
		 * Put a given package into this repository.
		 *
		 * @param pkg
		 */
		public void put(ZipFile pkg, String name, Semantic.Version version) throws IOException;
	}
}
