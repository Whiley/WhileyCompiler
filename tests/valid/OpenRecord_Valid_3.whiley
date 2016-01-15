

type Object is {
    function fn(Object)->int,
    ...
}

public function myFn(Object o) -> int:
    return 123

public function get(Object o) -> int:
    return o.fn(o)

public export method test() :
    Object o = {fn: &myFn}
    assume get(o) == 123
