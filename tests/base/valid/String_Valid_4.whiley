import whiley.lang.*:*

int|null indexOf(char c1, string str):
    i = 0
    for c2 in str:
        if c1 == c2:
            return i
        i = i + 1
    return null

void ::main(System sys,[string] args):
    sys.out.println(str(indexOf('H',"Hello World")))
    sys.out.println(str(indexOf('e',"Hello World")))
    sys.out.println(str(indexOf('l',"Hello World")))
    sys.out.println(str(indexOf('o',"Hello World")))
    sys.out.println(str(indexOf(' ',"Hello World")))
    sys.out.println(str(indexOf('W',"Hello World")))
    sys.out.println(str(indexOf('r',"Hello World")))
    sys.out.println(str(indexOf('d',"Hello World")))
    sys.out.println(str(indexOf('z',"Hello World")))
    sys.out.println(str(indexOf('1',"Hello World")))
