import * from whiley.lang.*

int|null indexOf(char c1, string str):
    i = 0
    for c2 in str:
        if c1 == c2:
            return i
        i = i + 1
    return null

void ::main(System sys,[string] args):
    sys.out.println(toString(indexOf('H',"Hello World")))
    sys.out.println(toString(indexOf('e',"Hello World")))
    sys.out.println(toString(indexOf('l',"Hello World")))
    sys.out.println(toString(indexOf('o',"Hello World")))
    sys.out.println(toString(indexOf(' ',"Hello World")))
    sys.out.println(toString(indexOf('W',"Hello World")))
    sys.out.println(toString(indexOf('r',"Hello World")))
    sys.out.println(toString(indexOf('d',"Hello World")))
    sys.out.println(toString(indexOf('z',"Hello World")))
    sys.out.println(toString(indexOf('1',"Hello World")))
