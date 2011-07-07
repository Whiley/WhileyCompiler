// replace all occurrences of "old" with "new" in string "str".  TO BE
// DEPRECATED
public bool has(char c, string str):
    i = 0
    while i < |str|:
        if str[i] == c:
            return true
        i = i + 1
    return false

void System::main([string] args):
    s = "Hello World"
    out.println(str(has('l',s)))
    out.println(str(has('e',s)))
    out.println(str(has('h',s)))
    out.println(str(has('z',s)))
    out.println(str(has('H',s)))
