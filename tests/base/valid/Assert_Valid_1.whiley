import println from whiley.lang.System

int f(int|[int] xs):
    // this is deliberately facetious
    if xs is [int]:
        y = 1
    else:
        y = 0
    // yup, you'd never do this right?
    if y == 1:
        assert xs is [int]
        return |xs|
    else:
        assert xs is int
        return xs

public void ::main(System.Console console):
    console.out.println(f([1,2,3]))
    console.out.println(f(123456))
