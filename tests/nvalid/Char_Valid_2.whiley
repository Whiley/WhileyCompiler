import println from whiley.lang.System

function iof(string s, int i) => char:
    return s[i]

method main(System.Console sys) => void:
    sys.out.println(Any.toString(iof("Hello", 0)))
    sys.out.println(Any.toString(iof("Hello", 1)))
    sys.out.println(Any.toString(iof("Hello", 2)))
    sys.out.println(Any.toString(iof("Hello", 3)))
    sys.out.println(Any.toString(iof("Hello", 4)))
