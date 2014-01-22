import println from whiley.lang.System

function add([int] v1, [int] v2) => [int]
requires |v1| == |v2|
ensures |$| == |v1|:
    i = 0
    while i < |v1| where (i >= 0) && (|v1| == |v2|):
        v1[i] = v1[i] + v2[i]
        i = i + 1
    return v1

method main(System.Console sys) => void:
    sys.out.println(Any.toString(add([1, 2, 3], [4, 5, 6])))
    sys.out.println(Any.toString(add([1], [4])))
    sys.out.println(Any.toString(add([], [])))
