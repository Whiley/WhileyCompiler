import * from whiley.lang.*

string f(string s, char c):
    return s + c

void ::main(System.Console sys,[string] args):
    sys.out.println(f("Hello Worl",'d'))    
