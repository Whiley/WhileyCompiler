

public function repl(int old, int n, int[] str) -> int[]:
    int i = 0
    while i < |str| where i >= 0:
        if str[i] == old:
            str[i] = n
        i = i + 1
    return str

public export method test() :
    int[] s = "Hello World"
    assume repl('l', '1', s) == "He11o Wor1d"
