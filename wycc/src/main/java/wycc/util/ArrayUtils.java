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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;

public class ArrayUtils {

	/**
	 * Return an integer array containing consecutive integers from a given
	 * start value upto (but not including) a given end value.
	 *
	 * @param start
	 *            The start value from which the range begins. This value is
	 *            always the first element of the final array (assuming it's not
	 *            empty).
	 * @param end
	 *            The value up to which (exclusively) the range extends. This
	 *            value is not in the final array. If this value equals or is
	 *            less than the start value, then the empty array is returned.
	 * @return
	 */
	public static int[] range(int start, int end) {
		if (end <= start) {
			return new int[0];
		} else {
			int[] rs = new int[Math.abs(end - start)];
			for (int i = start; i < end; ++i) {
				rs[i - start] = i;
			}
			return rs;
		}
	}

	/**
	 * Append two arrays of boolean type together, producing a fresh array whose
	 * length equals that of the first and second added together.
	 *
	 * @param lhs
	 *            The left-hand side. Elements of this array will be copied
	 *            first into the resulting array.
	 * @param rhs
	 *            The right-hand side. Elements of this array will be copied
	 *            last into the resulting array.
	 * @return
	 */
	public static boolean[] append(boolean[] lhs, boolean[] rhs) {
		boolean[] rs = java.util.Arrays.copyOf(lhs, lhs.length + rhs.length);
		System.arraycopy(rhs, 0, rs, lhs.length, rhs.length);
		return rs;
	}

	/**
	 * Append an integer item to the front of an array of integer type together,
	 * producing a fresh array whose length equals that the second plus one.
	 *
	 * @param lhs
	 *            The left-hand side. Elements of this array will be copied
	 *            first into the resulting array.
	 * @param rhs
	 *            The right-hand side. Elements of this array will be copied
	 *            last into the resulting array.
	 * @return
	 */
	public static int[] append(int lhs, int[] rhs) {
		int[] rs = new int[rhs.length+1];
		rs[0] = lhs;
		System.arraycopy(rhs, 0, rs, 1, rhs.length);
		return rs;
	}

	/**
	 * Append two integer items to the front of an array of integer type
	 * together, producing a fresh array whose length equals that of the third
	 * plus two.
	 *
	 * @param lhs
	 *            The left-hand side. Elements of this array will be copied
	 *            first into the resulting array.
	 * @param rhs
	 *            The right-hand side. Elements of this array will be copied
	 *            last into the resulting array.
	 * @return
	 */
	public static int[] append(int first, int second, int[] rhs) {
		int[] rs = new int[rhs.length+2];
		rs[0] = first;
		rs[1] = second;
		System.arraycopy(rhs, 0, rs, 2, rhs.length);
		return rs;
	}

	/**
	 * Append two arrays of integer type together, producing a fresh array whose
	 * length equals that of the first and second added together.
	 *
	 * @param lhs
	 *            The left-hand side. Elements of this array will be copied
	 *            first into the resulting array.
	 * @param rhs
	 *            The right-hand side. Elements of this array will be copied
	 *            last into the resulting array.
	 * @return
	 */
	public static int[] append(int[] lhs, int[] rhs) {
		int[] rs = java.util.Arrays.copyOf(lhs, lhs.length + rhs.length);
		System.arraycopy(rhs, 0, rs, lhs.length, rhs.length);
		return rs;
	}

	/**
	 * Append two arrays of unknown type together, producing a fresh array whose
	 * length equals that of the first and second added together.
	 *
	 * @param lhs
	 *            The left-hand side. Elements of this array will be copied
	 *            first into the resulting array.
	 * @param rhs
	 *            The right-hand side. Elements of this array will be copied
	 *            last into the resulting array.
	 * @return
	 */
	public static <T> T[] append(T[] lhs, T... rhs) {
		T[] rs = java.util.Arrays.copyOf(lhs, lhs.length + rhs.length);
		System.arraycopy(rhs, 0, rs, lhs.length, rhs.length);
		return rs;
	}

	/**
	 * Append an element onto an array of unknown type together, producing a
	 * fresh array whose length equals that of the second plus one.
	 *
	 * @param lhs
	 *            The left-hand side. This element will be copied
	 *            first into the resulting array.
	 * @param rhs
	 *            The right-hand side. Elements of this array will be copied
	 *            last into the resulting array.
	 * @return
	 */
	public static <T> T[] append(T lhs, T... rhs) {
		T[] rs = java.util.Arrays.copyOf(rhs, 1 + rhs.length);
		System.arraycopy(rhs, 0, rs, 1, rhs.length);
		rs[0] = lhs;
		return rs;
	}

	/**
	 * Append an element onto an array of unknown type together, producing a
	 * fresh array whose length equals that of the second plus one.
	 *
	 * @param lhs
	 *            The left-hand side. This element will be copied
	 *            first into the resulting array.
	 * @param rhs
	 *            The right-hand side. Elements of this array will be copied
	 *            last into the resulting array.
	 * @return
	 */
	public static <T> T[] append(Class<T> type, T lhs, T... rhs) {
		T[] rs = (T[]) Array.newInstance(type, rhs.length+1);
		System.arraycopy(rhs, 0, rs, 1, rhs.length);
		rs[0] = lhs;
		return rs;
	}

	/**
	 * Append an element onto an array of unknown type together, producing a
	 * fresh array whose length equals that of the second plus one.
	 *
	 * @param lhs
	 *            The left-hand side. This element will be copied
	 *            first into the resulting array.
	 * @param rhs
	 *            The right-hand side. Elements of this array will be copied
	 *            last into the resulting array.
	 * @return
	 */
	public static <T> T[] append(Class<T> type, T[] lhs, T rhs) {
		T[] rs = (T[]) Array.newInstance(type, lhs.length+1);
		System.arraycopy(lhs, 0, rs, 0, lhs.length);
		rs[lhs.length] = rhs;
		return rs;
	}

	/**
	 * Add all elements from an array into a given collection of the same type.
	 *
	 * @param lhs
	 *            The left-hand side. Elements of this array will be added to
	 *            the collection.
	 * @param rhs
	 *            The right-hand side. Elements from the left-hand side will be
	 *            added to this collection.
	 */
	public static <T> void addAll(T[] lhs, Collection<T> rhs) {
		for(int i=0;i!=lhs.length;++i) {
			rhs.add(lhs[i]);
		}
	}

	/**
	 * Convert a collection of strings into a string array.
	 *
	 * @param items
	 * @return
	 */
	public static String[] toStringArray(Collection<String> items) {
		String[] result = new String[items.size()];
		int i = 0;
		for(String s : items) {
			result[i++] = s;
		}
		return result;
	}

	/**
	 * Convert a collection of Integers into an int array.
	 *
	 * @param items
	 * @return
	 */
	public static int[] toIntArray(Collection<Integer> items) {
		int[] result = new int[items.size()];
		int i = 0;
		for (Integer v : items) {
			result[i++] = v;
		}
		return result;
	}

	/**
	 * Convert from an array of one kind to an array of another kind.
	 *
	 * @param type
	 * @param src
	 * @return
	 */
	public static <T,S> T[] toArray(Class<T> type, S[] src) {
		@SuppressWarnings("unchecked")
		T[] dest = (T[]) Array.newInstance(type, src.length);
		System.arraycopy(src, 0, dest, 0, src.length);
		return dest;
	}

	/**
	 * Convert from an array of one kind to an array of another kind.
	 *
	 * @param type
	 * @param src
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T,S> T[] toArray(Class<T> type, Collection<S> src) {
		T[] dest = (T[]) Array.newInstance(type, src.size());
		int i = 0;
		for(S s : src) {
			dest[i++] = (T) s;
		}
		return dest;
	}


	/**
	 * Remove duplicate types from an unsorted array. This produces a potentially
	 * smaller array with all duplicates removed. Null is permitted in the array
	 * and will be preserved, though duplicates of it will not be. Items in the
	 * array are compared using <code>Object.equals()</code>.
	 *
	 * @param items
	 *            The array for which duplicates are to be removed
	 * @return
	 */
	public static <T> T[] removeDuplicates(T[] items) {
		int count = 0;
		// First, identify duplicates and store this information in a bitset.
		BitSet duplicates = new BitSet(items.length);
		for (int i = 0; i != items.length; ++i) {
			T ith = items[i];
			for (int j = i + 1; j < items.length; ++j) {
				T jth = items[j];
				if(ith == null) {
					if(jth == null) {
						duplicates.set(i);
						count = count + 1;
						break;
					}
				} else if (ith.equals(jth)) {
					duplicates.set(i);
					count = count + 1;
					break;
				}
			}
		}
		// Second, eliminate duplicates (if any)
		if (count == 0) {
			// nothing actually needs to be removed
			return items;
		} else {
			T[] nItems = Arrays.copyOf(items, items.length - count);
			for (int i = 0, j = 0; i != items.length; ++i) {
				if (duplicates.get(i)) {
					// this is a duplicate, ignore
				} else {
					nItems[j++] = items[i];
				}
			}
			return nItems;
		}
	}

	/**
	 * Remove duplicate types from an sorted array, thus any duplicates are
	 * located adjacent to each other. This produces a potentially smaller array
	 * with all duplicates removed. Null is permitted in the array and will be
	 * preserved, though duplicates of it will not be. Items in the array are
	 * compared using <code>Object.equals()</code>.
	 *
	 * @param items
	 *            The array for which duplicates are to be removed
	 * @return
	 */
	public static <T> T[] sortedRemoveDuplicates(T[] items) {
		int count = 0;
		// First, identify duplicates and store this information in a bitset.
		BitSet duplicates = new BitSet(items.length);
		for (int i = 1; i < items.length; ++i) {
			T ith = items[i];
			T jth = items[i-1];
			if(ith != null) {
				if (ith.equals(jth)) {
					duplicates.set(i-1);
					count = count + 1;
				}
			} else if(jth == null) {
				duplicates.set(i-1);
				count = count + 1;
			}
		}
		// Second, eliminate duplicates (if any)
		if (count == 0) {
			// nothing actually needs to be removed
			return items;
		} else {
			T[] nItems = Arrays.copyOf(items, items.length - count);
			for (int i = 0, j = 0; i != items.length; ++i) {
				if (duplicates.get(i)) {
					// this is a duplicate, ignore
				} else {
					nItems[j++] = items[i];
				}
			}
			return nItems;
		}
	}


	/**
	 * Sort and remove duplicate items from a given array.
	 *
	 * @param children
	 * @return
	 */
	public static <T extends S, S extends Comparable<S>> T[] sortAndRemoveDuplicates(T[] children) {
		int r = isSortedAndUnique(children);
		switch (r) {
		case 0:
			// In this case, the array is already sorted and no duplicates were
			// found.
			return children;
		case 1:
			// In this case, the array is already sorted, but duplicates were
			// found
			return ArrayUtils.sortedRemoveDuplicates(children);
		default:
			// In this case, the array is not sorted and may or may not
			// contain duplicates.
			children = Arrays.copyOf(children, children.length);
			Arrays.sort(children);
			return ArrayUtils.sortedRemoveDuplicates(children);
		}
	}

	/**
	 * Check whether or not the children of this array are sorted according to
	 * their underlying order. And, if so, whether or not there are any
	 * duplicate elements encountered.
	 *
	 * @param children
	 * @return
	 */
	public static <T extends Comparable<T>> int isSortedAndUnique(T[] children) {
		int r = 0;
		for (int i = 1; i < children.length; ++i) {
			int c = children[i - 1].compareTo(children[i]);
			if (c == 0) {
				// Duplicate found, though still could be in sorted order.
				r = 1;
			} else if (c > 0) {
				// NOT in sorted order
				return -1;
			}
		}
		// All good
		return r;
	}

	/**
	 * Remove any occurrence of a given value from an array. The resulting array
	 * may be shorter in length, but the relative position of all other items
	 * will remain unchanged. This algorithm is robust to <code>null</code>. The
	 * <code>items</code> array may contain <code>null</code> values and the
	 * <code>item</code> may itself be <code>null</code> (in which case, all
	 * <code>null</code> values are removed).
	 *
	 * @param items
	 * @return
	 */
	public static <T> T[] removeAll(T[] items, T item) {
		int count = 0;
		// First, determine the number of elements which will be removed
		for (int i = 0; i != items.length; ++i) {
			T ith = items[i];
			if (ith == item || (item != null && item.equals(ith))) {
				count++;
			}
		}
		// Second, eliminate duplicates (if any)
		if (count == 0) {
			// nothing actually needs to be removed
			return items;
		} else {
			T[] nItems = Arrays.copyOf(items, items.length - count);
			for(int i=0, j = 0;i!=items.length;++i) {
				T ith = items[i];
				if (ith == item || (item != null && item.equals(ith))) {
					// skip item
				} else {
					nItems[j++] = ith;
				}
			}
			return nItems;
		}
	}

	/**
	 * A default operator for comparing arrays
	 *
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public static <S,T extends Comparable<S>> int compareTo(T[] lhs, T[] rhs) {
		if(lhs.length != rhs.length) {
			return lhs.length - rhs.length;
		} else {
			for(int i=0;i!=lhs.length;++i) {
				int r = lhs[i].compareTo((S) rhs[i]);
				if(r != 0) {
					return r;
				}
			}
			return 0;
		}
	}

	/**
	 * Determine the first index of a given item in an array of items, or return
	 * -1 otherwise. Items are compared using the method
	 * <code>Object.equals()</code>. The <code>items</code> array may contain
	 * <code>null</code> values; likewise, <code>item</code> may be
	 * <code>null</code> and will match against other <code>null</code> values.
	 *
	 * @param items
	 * @param item
	 * @return
	 */
	public static <T> int firstIndexOf(T[] items, T item) {
		for (int i = 0; i != items.length; ++i) {
			T ith = items[i];
			if (ith == item || (ith != null && ith.equals(item))) {
				return i;
			}
		}
		return -1;
	}
}
