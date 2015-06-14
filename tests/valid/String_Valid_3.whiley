

public function has(int c1, [int] str) -> bool:
    int i = 0
    while i < |str|:
        if c1 == str[i]:
            return true
        i = i + 1
    return false

public export method test() -> void:
    [int] s = "Hello World"
    assume has('l', s) == true
    assume has('e', s) == true
    assume has('h', s) == false
    assume has('z', s) == false
    assume has('H', s) == true
