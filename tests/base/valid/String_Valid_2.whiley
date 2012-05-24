import println from whiley.lang.System

// replace all occurrences of "old" with "new" in string "str".  TO BE
// DEPRECATED
public bool has(char c1, string str):
    for c2 in str:
        if c1 == c2:
            return true
    return false

void ::main(System.Console sys):
    s = "Hello World"
    sys.out.println(Any.toString(has('l',s)))
    sys.out.println(Any.toString(has('e',s)))
    sys.out.println(Any.toString(has('h',s)))
    sys.out.println(Any.toString(has('z',s)))
    sys.out.println(Any.toString(has('H',s)))
