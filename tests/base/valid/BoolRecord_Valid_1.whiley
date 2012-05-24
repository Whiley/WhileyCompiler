import println from whiley.lang.System

void ::main(System.Console sys):
    x = {flag:true,code:0}
    sys.out.println(Any.toString(x))
    x.flag = false
    sys.out.println(Any.toString(x))
