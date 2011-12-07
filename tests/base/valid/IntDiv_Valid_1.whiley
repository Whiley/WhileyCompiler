import * from whiley.lang.*

int f(int x, int y):
    return x / y

void ::main(System sys,[string] args):
     x = f(10,2)
     sys.out.println(Any.toString(x)  )
