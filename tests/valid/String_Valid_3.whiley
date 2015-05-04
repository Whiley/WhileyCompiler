

public function has(int c1, [int] str) -> bool:
    for c2 in str:
        if c1 == c2:
            return true
    return false

public export method test() -> void:
    [int] s = "Hello World"
    assume has('l', s) == true
    assume has('e', s) == true
    assume has('h', s) == false
    assume has('z', s) == false
    assume has('H', s) == true
