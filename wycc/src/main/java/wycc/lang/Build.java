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
package wycc.lang;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import wycc.util.Pair;

public interface Build {

	/**
	 * Represents a versioned view of the "build", including all generated
	 * artifacts.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Repository extends Content.Ledger {
		/**
		 * Apply a given transformer to this repository. This will be given the latest
		 * snapshot when it is executed. The resulting snapshot well then become the
		 * head (if no other snapshots have been written inbetween) or will be merged
		 * (if possible).
		 *
		 * @param transformer
		 */
		public void apply(Function<SnapShot, SnapShot> transformer);

		/**
		 * Get the ith state within this repository
		 *
		 * @param i
		 * @return
		 */
		@Override
		public SnapShot get(int i);

		/**
		 * Get current state of the build system
		 *
		 * @return
		 */
		public SnapShot last();

		@Override
		public <T extends Content> T get(Content.Type<T> kind, Path p);

		@Override
		public <T extends Content> List<T> getAll(Content.Type<T> kind, Filter f);
	}

	/**
	 * Represents a snapshot of the repository at a given point in time.
	 *
	 * @param <S>
	 */
	public interface SnapShot extends Content.Source, Iterable<Build.Artifact> {
		@Override
		public <T extends Content> T get(Content.Type<T> kind, Path p);

		@Override
		public <T extends Content> List<T> getAll(Content.Type<T> kind, Filter f);

		/**
		 * Write a specific artifact to this snapshot, thereby producing a new snapshot.
		 *
		 * @param entry
		 * @param <T>
		 * @return
		 */
		public <T extends Artifact> SnapShot put(T entry);
	}

    /**
     * Represents a given "build artifact" within a repository.  This could a SourceFile, or some kind of structured
     * syntax tree or intermediate representation.  It could also be a binary target.
     */
	public interface Artifact extends Content {
    	/**
		 * Get the location within the build of this artifact.
		 *
		 * @return
		 */
        public Path getPath();

        /**
		 * Get the content type of this artifact.
		 *
		 * @return
		 */
        @Override
		public Content.Type<? extends Artifact> getContentType();

        /**
		 * Get all the source artifacts that contributed to this artifact. Observe that,
		 * if this is a source file, then this list is always empty!
		 *
		 * @return
		 */
		public List<? extends Build.Artifact> getSourceArtifacts();
    }

	/**
	 * A shortlived unit of work responsible for generating a given build artifact
	 * (e.g. converting one or more files of a given type into a given target file).
	 * Tasks which are not dependent on each other may be scheduled in parallel.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Task extends Artifact, Function<SnapShot, Pair<SnapShot, Boolean>> {
	}

    /**
     * Responsible for recording detailed progress of a given task for both informational and profiling purposes. For
     * example, providing feedback on expected time to completion in an IDE. Or, providing detailed feedback on number
     * of steps executed by key components in a given task, etc.
     *
     * @author David J. Pearce
     */
    public interface Meter {
        /**
         * Create subtask of current task with a given name.
         *
         * @return
         */
        public Meter fork(String name);

        /**
         * Record an arbitrary step taking during this subtask for profiling purposes.
         *
         * @param tag
         */
        public void step(String tag);

        /**
         * Current (sub)task has completed.
         */
        public void done();
    }

    public interface Stage {

    }

    public static final Build.Meter NULL_METER = new Build.Meter() {

        @Override
        public Meter fork(String name) {
            return NULL_METER;
        }

        @Override
        public void step(String tag) {

        }

        @Override
        public void done() {
        }

    };
}
