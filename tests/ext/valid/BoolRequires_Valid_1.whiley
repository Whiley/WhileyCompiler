import println from whiley.lang.System

string f([bool] x) requires |x| > 0 && x[0]:
    return Any.toString(x)

void ::main(System.Console sys):
    sys.out.println(f([true]))
    sys.out.println(f([true,false]))
    sys.out.println(f([true,false,true]))
    
