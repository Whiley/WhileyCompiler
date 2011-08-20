import whiley.lang.*:*

[int8] f(int x) requires x == 0 || x == 169:
    return [x]

void System::main([string] args):
    bytes = f(0)
    this.out.println(str(bytes))

