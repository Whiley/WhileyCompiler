import println from whiley.lang.System

int g(int x, int y):
    return x + y

int(int) f1(int x):
    return &(int y -> g(x,y))

int(int) f2(int y):
    return &(int x -> g(x,y))

public void ::main(System.Console console):
    fx = f1(10)
    fy = f2(20)
    console.out.println(fx(1))
    console.out.println(fy(1))