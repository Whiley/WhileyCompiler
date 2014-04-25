import whiley.lang.System

public function has(char c, string str) => bool:
    int i = 0
    while i < |str| where i >= 0:
        if str[i] == c:
            return true
        i = i + 1
    return false

method main(System.Console sys) => void:
    string s = "Hello World"
    sys.out.println(Any.toString(has('l', s)))
    sys.out.println(Any.toString(has('e', s)))
    sys.out.println(Any.toString(has('h', s)))
    sys.out.println(Any.toString(has('z', s)))
    sys.out.println(Any.toString(has('H', s)))
