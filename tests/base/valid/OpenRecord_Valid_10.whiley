import * from whiley.lang.System

public define Object as {
    int(Object) fn,
    ...
}

public int myFn(Object o):
    return 123

public int get(Object o):
    return o.fn(o)

public void ::main(System.Console sys):
    o = { fn: &myFn }
    sys.out.println(get(o))

