import println from whiley.lang.*

define nat as int where $ >= 0

nat abs(int item):
    return Math.abs(item)

void ::main(System.Console sys):
    xs = abs(-1)
    sys.out.println(Any.toString(xs))
