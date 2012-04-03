import println from whiley.lang.System

// replace all occurrences of "old" with "new" in string "str".  TO BE
// DEPRECATED
public string repl(char old, char n, string str):
    i = 0
    while i < |str|:
        if str[i] == old:
            str[i] = n
        i = i + 1
    return str    

void ::main(System.Console sys):
    s = "Hello World"
    s = repl('l','1',s)
    sys.out.println(s)
