import println from whiley.lang.System

define etup as (int,int)|(real,real)

etup f(int x):
    if x < 0:
        return 1,2
    else:
        return 1.2,2.3

public void ::main(System.Console sys):
    x,y = f(-1)
    sys.out.println("X=" + x + ",Y=" + y)
    x,y = f(2)
    sys.out.println("X=" + x + ",Y=" + y)
