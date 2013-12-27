import println from whiley.lang.System

int g(int x):
    if(x <= 0 || x >= 125):
        return 1
    else:
        return x

{int} f(int x):
    return {g(x)}

void ::main(System.Console sys):
    bytes = f(0)
    sys.out.println(Any.toString(bytes))

