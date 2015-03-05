import whiley.lang.System

public function has(ASCII.char c1, ASCII.string str) -> bool:
    for c2 in str:
        if c1 == c2:
            return true
    return false

method main(System.Console sys) -> void:
    ASCII.string s = "Hello World"
    sys.out.println(Any.toString(has('l', s)))
    sys.out.println(Any.toString(has('e', s)))
    sys.out.println(Any.toString(has('h', s)))
    sys.out.println(Any.toString(has('z', s)))
    sys.out.println(Any.toString(has('H', s)))
