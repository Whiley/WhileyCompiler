import println from whiley.lang.System

int|null indexOf(char c1, string str):
    i = 0
    for c2 in str:
        if c1 == c2:
            return i
        i = i + 1
    return null

void ::main(System.Console sys):
    sys.out.println(Any.toString(indexOf('H',"Hello World")))
    sys.out.println(Any.toString(indexOf('e',"Hello World")))
    sys.out.println(Any.toString(indexOf('l',"Hello World")))
    sys.out.println(Any.toString(indexOf('o',"Hello World")))
    sys.out.println(Any.toString(indexOf(' ',"Hello World")))
    sys.out.println(Any.toString(indexOf('W',"Hello World")))
    sys.out.println(Any.toString(indexOf('r',"Hello World")))
    sys.out.println(Any.toString(indexOf('d',"Hello World")))
    sys.out.println(Any.toString(indexOf('z',"Hello World")))
    sys.out.println(Any.toString(indexOf('1',"Hello World")))
