import println from whiley.lang.System

void ::main(System.Console sys):    
    if |sys.args| == 1:
        x = 1
    else:
        x = [1,2,3]
    sys.out.println(Any.toString(x))

