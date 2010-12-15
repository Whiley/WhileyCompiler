{int->int} f(int x):
    return {1->x, 3->2}

int get(int i, {int->int} map):
    return map[i]

void System::main([string] args):
    out->println(str(get(1,f(1))))
    out->println(str(get(1,f(2))))
    out->println(str(get(1,f(3))))
    out->println(str(get(3,f(3))))
