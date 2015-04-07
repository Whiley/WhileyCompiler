import whiley.lang.*

type Object is {
    function fn(Object)->int,
    ...
}

public function myFn(Object o) -> int:
    return 123

public function get(Object o) -> int:
    return o.fn(o)

public method main(System.Console sys) -> void:
    Object o = {fn: &myFn}
    sys.out.println(get(o))
