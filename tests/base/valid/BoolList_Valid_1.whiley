import println from whiley.lang.System

void ::main(System.Console sys):
    ls = [true,false,true]
    sys.out.println(Any.toString(ls))
    x = ls[0]
    sys.out.println(Any.toString(x))
    ls[0] = false
    sys.out.println(Any.toString(ls))
