// find first index after a given start point in list which matches character.
// If no match, then return null.
public function first_index_of<T>(T[] items, T item, int start) -> (int|null index)
// Starting point cannot be negative
requires start >= 0
// If null returned, no element in items matches item
ensures index is null ==> all { i in 0 .. |items| | items[i] != item }:
    //
    int i = start
    //
    while i < |items|
    // i is positive
    where i >= 0
    // No element seen so far matches item
    where all { j in start .. i | items[j] != item }:
        //
        if items[i] == item:
            return i
        i = i + 1
    //
    return null
