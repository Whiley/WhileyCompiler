import println from whiley.lang.System

define posintlist as [int] where no { x in $ | x < 0 }

int sum(posintlist ls) ensures $ >= 0:
    if(|ls| == 0):
        return 0
    else:
        rest = ls[1..]
        return ls[0] + sum(rest)

void ::main(System.Console sys):
    c = sum([1,2,3,4,5,6,7])
    sys.out.println(Any.toString(c))
    
