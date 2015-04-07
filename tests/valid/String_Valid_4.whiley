import whiley.lang.*

public function repl(int old, int n, [int] str) -> [int]:
    int i = 0
    while i < |str| where i >= 0:
        if str[i] == old:
            str[i] = n
        i = i + 1
    return str

method main(System.Console sys) -> void:
    [int] s = "Hello World"
    s = repl('l', '1', s)
    sys.out.println_s(s)
