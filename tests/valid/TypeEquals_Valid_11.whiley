import println from whiley.lang.System

define nlist as int|[int]

int f(int i, [nlist] xs):
    if i < 0 || i >= |xs|:
        return 0
    else if xs[i] is int:
        return xs[i]
    else:
        return 0

void ::main(System.Console sys):
    x = f(2, [2,3,4])    
    sys.out.println(Any.toString(x))
