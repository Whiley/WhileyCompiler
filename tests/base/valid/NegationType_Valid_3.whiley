import whiley.lang.*:*

!null&!int f(string x):
    return x

void ::main(System sys, [string] args):
    sys.out.println(String.str(f("Hello World")))
