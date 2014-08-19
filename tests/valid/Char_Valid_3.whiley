import whiley.lang.System

function rep(char old, char n, string str) => string:
    int i = 0
    while i < |str| where i >= 0:
        if str[i] == old:
            str[i] = n
        i = i + 1
    return str

method main(System.Console sys) => void:
    sys.out.println(rep('e', 'w', "Hello"))
    sys.out.println(rep('H', 'z', "Hello"))
    sys.out.println(rep('o', '1', "Hello"))
