import whiley.lang.*:*

int f([int] x):
    switch x:
        case []:
            return 0
        case [1]:
            return -1
    return 10

void System::main([string] args):
    this.out.println(str(f([])))
    this.out.println(str(f([1])))
    this.out.println(str(f([3])))
    this.out.println(str(f([1,2,3])))
