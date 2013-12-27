import println from whiley.lang.System

{int} f(int x):
    return {x}

void ::main(System.Console sys):
    bytes = f(0)
    sys.out.println(Any.toString(bytes))

