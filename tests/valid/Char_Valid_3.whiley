import println from whiley.lang.System

string rep(char old, char n, string str):
    i = 0
    while i < |str|:
        if str[i] == old:
            str[i] = n
        i = i + 1
    return str    


void ::main(System.Console sys):
    sys.out.println(rep('e','w',"Hello"))
    sys.out.println(rep('H','z',"Hello"))
    sys.out.println(rep('o','1',"Hello"))
