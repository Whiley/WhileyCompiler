import println from whiley.lang.System

string f(string s, char c):
    return s + c

void ::main(System.Console sys):
    sys.out.println(f("Hello Worl",'d'))    
