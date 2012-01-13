import * from whiley.lang.*

Type.nat f(int x):
    if x < 0:
        return 0
    else:
        return x

public void ::main(System.Console sys):
    sys.out.println(Any.toString(f(1)))
