import whiley.lang.System

public function has(char c1, string str) => bool:
    for c2 in str:
        if c1 == c2:
            return true
    return false

method main(System.Console sys) => void:
    string s = "Hello World"
    sys.out.println(Any.toString(has('l', s)))
    sys.out.println(Any.toString(has('e', s)))
    sys.out.println(Any.toString(has('h', s)))
    sys.out.println(Any.toString(has('z', s)))
    sys.out.println(Any.toString(has('H', s)))
