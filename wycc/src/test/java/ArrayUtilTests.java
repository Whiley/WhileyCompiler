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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.*;

import wycc.util.ArrayUtils;

public class ArrayUtilTests {
	@Test public void range_1() {
		assertTrue(Arrays.equals(ArrayUtils.range(0, 0), new int[]{}));
	}
	@Test public void range_2() {
		assertTrue(Arrays.equals(ArrayUtils.range(0, 1), new int[]{0}));
	}
	@Test public void range_3() {
		assertTrue(Arrays.equals(ArrayUtils.range(0, 2), new int[]{0,1}));
	}
	@Test public void range_4() {
		assertTrue(Arrays.equals(ArrayUtils.range(-1, 0), new int[]{-1}));
	}
	@Test public void range_5() {
		assertTrue(Arrays.equals(ArrayUtils.range(0, -1), new int[]{}));
	}
}
