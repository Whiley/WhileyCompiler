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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import wycc.util.TextFile;
import wycc.util.Trie;

public interface TestStage {
	/**
	 * Encapsulates the result from a given state.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Result  {
		public final TestFile.Error[] markers;
		public final boolean ignored;
		public Result(boolean ignored, TestFile.Error... markers) {
			this.markers = markers;
			this.ignored = ignored;
		}
	}
	/**
	 * Apply this stage in a given directory, using configuration as appropriate
	 * from the test file.
	 *
	 * @param dir
	 * @param tf
	 * @return
	 */
	public Result apply(Trie path, Path dir, Map<Trie, TextFile> state, TestFile tf) throws IOException;

	/**
	 * Filter out all errors which could not be reported by this stage.
	 *
	 * @param errors
	 * @return
	 */
	public TestFile.Error[] filter(TestFile.Error[] errors);

	/**
	 * If true, then subsequent stages will not run if this stage produces errors
	 * (regardless of whether or not that was expected).
	 *
	 * @return
	 */
	public boolean required();
}
