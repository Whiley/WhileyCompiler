import println from whiley.lang.System

string f(string str, int end):
    return str[0..end]

void ::main(System.Console sys):
    str = "Hello Cruel World"
    sys.out.println(f(str,0))
    sys.out.println(f(str,1))
    sys.out.println(f(str,5))
    sys.out.println(f(str,10))
