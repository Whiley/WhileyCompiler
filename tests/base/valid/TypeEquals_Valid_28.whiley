import whiley.lang.*:*

[int] f([[real|int]] e):
    if e is [[int]]:
        return e[0]
    else:
        return [1,2,3]

void System::main([string] args):
    this.out.println(str(f([[1,2,3,4,5,6,7]])))
    this.out.println(str(f([[]])))
    this.out.println(str(f([[1,2,2.01]])))
    this.out.println(str(f([[1.23,2,2.01]])))
