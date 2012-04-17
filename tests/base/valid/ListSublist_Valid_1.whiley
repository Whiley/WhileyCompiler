import println from whiley.lang.System

define posintlist as [int]

int sum(posintlist ls):
    if(|ls| == 0):
        return 0
    else:
        rest = ls[1..|ls|]
        return ls[0] + sum(rest)

void ::main(System.Console sys):
    c = sum([1,2,3,4,5,6,7])
    sys.out.println(Any.toString(c))
    
