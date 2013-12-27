import println from whiley.lang.System

void ::main(System.Console sys):
    x = 1
    map = {1=>x, 3=>2}
    sys.out.println(Any.toString(map))
