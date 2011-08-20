import whiley.lang.*:*

void System::main([string] args):
    left = [1,2]
    right = [3,4]
    r = left + right
    left = left + [6]
    this.out.println(str(left))
    this.out.println(str(right))
    this.out.println(str(r))
