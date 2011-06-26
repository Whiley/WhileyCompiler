{int->int} f(int x):
    return {1->x, 3->2}

void System::main([string] args):
    out.println(str(f(1)))
    out.println(str(f(2)))
    out.println(str(f(3)))
