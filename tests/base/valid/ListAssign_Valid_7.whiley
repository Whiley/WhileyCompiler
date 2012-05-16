import println from whiley.lang.System

define intlist as int|[int]

string f([intlist] l):    
    return Any.toString(l)

void ::main(System.Console sys):
    if |sys.args| == 0:
        x = [1,2,3]
    else:
        x = [[1],[2,3],[5]]
    x[0] = 1
    sys.out.println(f(x))
