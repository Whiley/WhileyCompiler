import * from whiley.lang.*

int ::get():
    return 1

[int] ::f():
    return [1,2,3,get()]

void ::main(System.Console sys):
    proc = new { state: 1 }
    sys.out.println(Any.toString(f()))
