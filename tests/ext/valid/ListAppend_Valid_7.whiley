import println from whiley.lang.System

define nat as int where $ >= 0

nat f([int] xs, [int] ys):
    return |(xs + ys)|

void ::main(System.Console sys):
    left = [1,2,3]
    right = [5,6,7]
    r = f(left,right)
    sys.out.println(Any.toString(r))
