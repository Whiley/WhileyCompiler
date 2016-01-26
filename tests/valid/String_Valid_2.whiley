

public function has(int c, int[] str) -> bool:
    int i = 0
    while i < |str| where i >= 0:
        if str[i] == c:
            return true
        i = i + 1
    return false

public export method test() :
    int[] s = "Hello World"
    assume has('l', s) == true
    assume has('e', s) == true
    assume has('h', s) == false
    assume has('z', s) == false
    assume has('H', s) == true
