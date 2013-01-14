import println from whiley.lang.System

define nat as int where $ >= 0

// Initialise an list to a given size using a given default
// value for all cells.
[int] init(nat length, int value) ensures |$| == length && all { i in $ | i == value }:
    i = 0
    data = []
    while i != length where i == |data| && all { d in data | d == value }:
        data = data + [value]
        i = i + 1
    return data

void ::main(System.Console sys):
    for i in 0 .. 10:
        sys.out.println(Any.toString(init(i,i)))
    










    
    
    