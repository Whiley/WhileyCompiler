import println from whiley.lang.System

public function repl(char old, char n, string str) => string:
    i = 0
    while i < |str|:
        if str[i] == old:
            str[i] = n
        i = i + 1
    return str

method main(System.Console sys) => void:
    s = "Hello World"
    s = repl('l', '1', s)
    sys.out.println(s)
