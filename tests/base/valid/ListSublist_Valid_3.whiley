import println from whiley.lang.System

define posintlist as [int]

int sum(posintlist ls):
    if(|ls| == 0):
        return 0
    else:
        rest = ls[1..]
        return ls[0] + sum(rest)

void ::main(System.Console sys):
    c = sum([-12987987234,-1,2,409234,2398729879])
    sys.out.println(Any.toString(c))
    
