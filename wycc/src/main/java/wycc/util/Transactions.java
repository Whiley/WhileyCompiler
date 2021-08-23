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

import java.util.Iterator;
import java.util.List;

import wycc.lang.Build;

public class Transactions {
	/**
	 * Construct a simple sequential transaction from an array of tasks.
	 *
	 * @param tasks
	 * @return
	 */
	public static Build.Transaction create(Build.Task...tasks) {
		return new Build.Transaction() {

			@Override
			public Iterator<Build.Task> iterator() {
				return ArrayUtils.iterator(tasks);
			}

			@Override
			public int size() {
				return tasks.length;
			}

			@Override
			public Build.Task get(int ith) {
				return tasks[ith];
			}
		};
	}

	/**
	 * Construct a simple sequential transaction from a list of tasks.
	 *
	 * @param tasks
	 * @return
	 */
	public static Build.Transaction create(List<Build.Task> tasks) {
		return new Build.Transaction() {

			@Override
			public Iterator<Build.Task> iterator() {
				return tasks.iterator();
			}

			@Override
			public int size() {
				return tasks.size();
			}

			@Override
			public Build.Task get(int ith) {
				return tasks.get(ith);
			}
		};
	}
}
