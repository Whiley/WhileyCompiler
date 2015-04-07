import whiley.lang.*

public function has(int c, [int] str) -> bool:
    int i = 0
    while i < |str| where i >= 0:
        if str[i] == c:
            return true
        i = i + 1
    return false

method main(System.Console sys) -> void:
    [int] s = "Hello World"
    sys.out.println(has('l', s))
    sys.out.println(has('e', s))
    sys.out.println(has('h', s))
    sys.out.println(has('z', s))
    sys.out.println(has('H', s))
