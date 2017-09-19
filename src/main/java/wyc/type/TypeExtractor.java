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
package wyc.type;

import static wyc.lang.WhileyFile.*;
import wybs.lang.NameResolver.ResolutionError;

public interface TypeExtractor<T,S> {
	/**
	 * Attempt to extract a certain kind of type from an arbitrary type. This is
	 * necessary in the presence of powerful type connectives such as
	 * <i>union</i>, <i>intersection</i> and <i>negation</i>. For example, given
	 * the type <code>{int x}|{int x}</code> we can extract the type
	 * <code>{int x}</code>.
	 *
	 * @param type
	 *            The type for which information is to be extracted
	 * @param supplementary
	 *            Supplementary information which may be used by the extractor.
	 * @return
	 */
	public T extract(Type type, S supplementary) throws ResolutionError;
}
