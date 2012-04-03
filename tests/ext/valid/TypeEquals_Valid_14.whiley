import println from whiley.lang.System

define nat as int where $ >= 0
define T as int|[int]

int f(T x):
    if x is [int]|nat:
        return 0
    else:
        return x

void ::main(System.Console sys):
    sys.out.println(f(1))
    sys.out.println(f(-1))
    sys.out.println(f([1,2,3]))
