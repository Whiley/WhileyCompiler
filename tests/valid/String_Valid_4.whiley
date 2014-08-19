import whiley.lang.System

public function repl(char old, char n, string str) => string:
    int i = 0
    while i < |str| where i >= 0:
        if str[i] == old:
            str[i] = n
        i = i + 1
    return str

method main(System.Console sys) => void:
    string s = "Hello World"
    s = repl('l', '1', s)
    sys.out.println(s)
