import * from whiley.lang.*

define nlist as int|[nat]

nlist f(int i, [nlist] xs):
    if i < 0 || i >= |xs|:
        return 0
    else:
        return xs[i]

void ::main(System.Console sys,[string] args):
    x = f(2, [2,3,4])    
    sys.out.println(Any.toString(x))
