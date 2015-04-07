import whiley.lang.*

public function has(int c1, [int] str) -> bool:
    for c2 in str:
        if c1 == c2:
            return true
    return false

method main(System.Console sys) -> void:
    [int] s = "Hello World"
    sys.out.println(has('l', s))
    sys.out.println(has('e', s))
    sys.out.println(has('h', s))
    sys.out.println(has('z', s))
    sys.out.println(has('H', s))
