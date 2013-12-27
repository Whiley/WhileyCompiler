import println from whiley.lang.System

define pintset as {int}

void ::main(System.Console sys):
    p = {'b','c'}
    sys.out.println(Any.toString(p))
