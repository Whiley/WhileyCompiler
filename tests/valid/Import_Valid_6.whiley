import println from whiley.lang.System
import whiley..* 

int f(File.Reader r):
    return 1

void ::main(System.Console sys):
    x = 1
    y = 2
    sys.out.println(x+y)
