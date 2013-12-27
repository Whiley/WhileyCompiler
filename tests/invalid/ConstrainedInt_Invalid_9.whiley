

// this is a comment!
define num as {1,2,3,4}

void f(num x):
    y = x
    debug Any.toString(y)

void g(int x, int z) requires (x == 0 || x == 1) && z in {1,2,3,x}:
    f(z)

void ::main(System.Console sys):
    g(0,0)
