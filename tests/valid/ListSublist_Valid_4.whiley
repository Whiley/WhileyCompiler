import println from whiley.lang.System

define nat as int where $ >= 0

[nat] tail([int] ls) requires |ls| > 0 && all { i in 1 .. |ls| | ls[i] >= 0 }:
    return ls[1..]


void ::main(System.Console sys):
    sys.out.println(tail([1,2,3,4]))
    sys.out.println(tail([1]))