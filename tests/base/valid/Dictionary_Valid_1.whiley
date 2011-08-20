import whiley.lang.*:*

void System::main([string] args):
    x = 1
    map = {1->x, 3->2}
    this.out.println(str(map))
