import println from whiley.lang.System

define Point as {int x,int y} where x != y

int f(int x):
    return x

Point Point(int i, int j):
    if f(i) != f(j):
        return {x: i, y: j}
    else:
        return {x: 1, y: -1}


void ::main(System.Console sys):
    rs = Point(1,1)
    sys.out.println(Any.toString(rs))
    rs = Point(1,2)
    sys.out.println(Any.toString(rs))








