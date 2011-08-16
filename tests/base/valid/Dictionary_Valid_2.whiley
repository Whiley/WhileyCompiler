{int->int} f(int x):
    return {1->x, 3->2}

void System::main([string] args):
    this.out.println(str(f(1)))
    this.out.println(str(f(2)))
    this.out.println(str(f(3)))
