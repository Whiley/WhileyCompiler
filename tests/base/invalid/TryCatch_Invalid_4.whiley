import * from whiley.lang.*

int f(int x):
    return x

void ::main(System sys,[string] args):
    x = 1
    try:
        sys.out.println(str(f(1)))
        sys.out.println(str(f(0)))
        sys.out.println(str(f(-1)))
    catch(int e):
        sys.out.println("CAUGHT EXCEPTION: " + x)
    sys.out.println("DONE")        