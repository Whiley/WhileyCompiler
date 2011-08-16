int|null indexOf(char c1, string str):
    i = 0
    for c2 in str:
        if c1 == c2:
            return i
        i = i + 1
    return null

void System::main([string] args):
    this.out.println(str(indexOf('H',"Hello World")))
    this.out.println(str(indexOf('e',"Hello World")))
    this.out.println(str(indexOf('l',"Hello World")))
    this.out.println(str(indexOf('o',"Hello World")))
    this.out.println(str(indexOf(' ',"Hello World")))
    this.out.println(str(indexOf('W',"Hello World")))
    this.out.println(str(indexOf('r',"Hello World")))
    this.out.println(str(indexOf('d',"Hello World")))
    this.out.println(str(indexOf('z',"Hello World")))
    this.out.println(str(indexOf('1',"Hello World")))
