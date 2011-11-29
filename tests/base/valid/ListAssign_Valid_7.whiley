import * from whiley.lang.*

define intlist as int|[int]

string f([intlist] l):    
    return toString(l)

void ::main(System sys,[string] args):
    if |args| == 0:
        x = [1,2,3]
    else:
        x = [[1],[2,3],[5]]
    x[0] = 1
    sys.out.println(f(x))
