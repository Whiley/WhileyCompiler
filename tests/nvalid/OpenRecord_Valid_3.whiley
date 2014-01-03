import println from whiley.lang.System

type Object is {int(Object) fn, ...}

public function myFn(Object o) => int:
    return 123

public function get(Object o) => int:
    return o.fn(o)

public method main(System.Console sys) => void:
    o = {fn: &myFn}
    sys.out.println(get(o))
