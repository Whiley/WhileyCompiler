import whiley.lang.*

function add([int] v1, [int] v2) -> ([int] vr)
// Input vectors must have same length
requires |v1| == |v2|
// Returned vector must have same length as inputs
ensures |vr| == |v1|:
    //
    int i = 0
    while i < |v1| where (i >= 0) && (|v1| == |v2|):
        v1[i] = v1[i] + v2[i]
        i = i + 1
    return v1

method main(System.Console sys) -> void:
    sys.out.println(add([1, 2, 3], [4, 5, 6]))
    sys.out.println(add([1], [4]))
    sys.out.println(add([], []))
