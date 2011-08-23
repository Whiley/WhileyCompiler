import whiley.lang.*:*

string f(string s, char c):
    return s + c

void ::main(System sys,[string] args):
    sys.out.println(f("Hello Worl",'d'))    
