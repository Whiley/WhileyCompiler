import println from whiley.lang.System

void ::main(System.Console sys):
    u = 0
    l = 0
    w = 0
    d = 0
    s = 0
    for i in 1..127:
        c = (char) i
        if (Char.isUpperCase(c)):
            u = u + 1
        if (Char.isLowerCase(c)):
            l = l + 1
        if (Char.isLetter(c)):
            w = w + 1
        if (Char.isDigit(c)):
            d = d + 1
        if (Char.isWhiteSpace(c)):
            s = s + 1
    sys.out.println(u)
    sys.out.println(l)
    sys.out.println(w)
    sys.out.println(d)
    sys.out.println(s)
