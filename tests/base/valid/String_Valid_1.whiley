import println from whiley.lang.System

// replace all occurrences of "old" with "new" in string "str".  TO BE
// DEPRECATED
public bool has(char c, string str):
    i = 0
    while i < |str|:
        if str[i] == c:
            return true
        i = i + 1
    return false

void ::main(System.Console sys):
    s = "Hello World"
    sys.out.println(Any.toString(has('l',s)))
    sys.out.println(Any.toString(has('e',s)))
    sys.out.println(Any.toString(has('h',s)))
    sys.out.println(Any.toString(has('z',s)))
    sys.out.println(Any.toString(has('H',s)))
