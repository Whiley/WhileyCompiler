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
package wycli.cfg;

import wycc.lang.Filter;
import wycc.lang.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * Combines one or more configurations into a single configuration. The
 * different configurations must be "compatible" in the sense that they are not
 * permitted overlapping keys.
 *
 * @author David J. Pearce
 *
 */
public class ConfigurationCombinator implements Configuration {
	private final Configuration.Schema schema;
	private final Configuration[] configurations;

	public ConfigurationCombinator(Configuration... configurations) {
		this.schema = toCombinedSchema(configurations);
		this.configurations = configurations;
	}

	@Override
	public Schema getConfigurationSchema() {
		return schema;
	}

	@Override
	public boolean hasKey(Path key) {
		for(int i=0;i!=configurations.length;++i) {
			if(configurations[i].hasKey(key)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<Path> matchAll(Filter filter) {
		ArrayList<Path> results = new ArrayList<>();
		for(int i=0;i!=configurations.length;++i) {
			results.addAll(configurations[i].matchAll(filter));
		}
		return results;
	}

	@Override
	public <T> T get(Class<T> kind, Path key) {
		for (int i = 0; i != configurations.length; ++i) {
			Configuration config = configurations[i];
			if (config.getConfigurationSchema().isKey(key)) {
				return config.get(kind, key);
			}
		}
		throw new IllegalArgumentException("invalid key access: " + key);
	}

	@Override
	public <T> void write(Path key, T value) {
		for (int i = 0; i != configurations.length; ++i) {
			Configuration config = configurations[i];
			if (config.getConfigurationSchema().isKey(key)) {
				config.write(key, value);
				return;
			}
		}
		throw new IllegalArgumentException("invalid key access: " + key);
	}

	private static Schema toCombinedSchema(Configuration... configurations) {
		Schema[] schemas = new Schema[configurations.length];
		// Get array of schemas
		for (int i = 0; i != schemas.length; ++i) {
			schemas[i] = configurations[i].getConfigurationSchema();
		}
		//
		return Configuration.toCombinedSchema(schemas);
	}

	@Override
	public String toString() {
		String r = "";
		for(int i=0;i!=configurations.length;++i) {
			r += configurations[i].toString();
		}
		return r;
	}
}
