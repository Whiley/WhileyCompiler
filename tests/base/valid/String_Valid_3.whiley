import * from whiley.lang.*

// replace all occurrences of "old" with "new" in string "str".  TO BE
// DEPRECATED
public string repl(char old, char new, string str):
    i = 0
    while i < |str|:
        if str[i] == old:
            str[i] = new
        i = i + 1
    return str    

void ::main(System.Console sys,[string] args):
    s = "Hello World"
    s = repl('l','1',s)
    sys.out.println(s)
