define point as {int x, int y} where $.x > 0 && $.y > 0

void System::main([string] args):
    p = {x:1,y:1}
    this.out.println(str(p))
