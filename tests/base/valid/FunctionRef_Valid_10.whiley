import println from whiley.lang.System

define efun as int(int) | real(int)

int|real f(int x, efun fn):
    return fn(x)

int g(int x):
    return x+1

real h(int x):
    return x+1.5

public void ::main(System.Console sys):
    x,y = f(-1,&g)
    sys.out.println("X=" + x + ",Y=" + y)
    x,y = f(2,&h)
    sys.out.println("X=" + x + ",Y=" + y)
