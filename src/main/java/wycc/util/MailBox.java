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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

public interface MailBox<T> {
	/**
	 * Send a message into this mailbox.
	 *
	 * @param mesage
	 */
	public void send(T mesage);

	public static class Buffered<T> implements MailBox<T>, Iterable<T> {
		private final ArrayList<T> buffer = new ArrayList<>();

		@Override
		public void send(T message) {
			buffer.add(message);
		}

		public Stream<T> stream() {
			return buffer.stream();
		}

		@Override
		public Iterator<T> iterator() {
			return buffer.iterator();
		}

		public int size() {
			return buffer.size();
		}

		public T get(int index) {
			return buffer.get(index);
		}

	}
}
