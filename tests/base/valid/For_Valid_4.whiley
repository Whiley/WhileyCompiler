real f({int->real} xs, int m):
    for i->r in xs:
        if i == m:
            return r
    return -1

void System::main([string] args):
    x = f({1->2.2,5->3.3},5)
    out<->println(str(x))
    x = f({1->2.2,5->3.3},2)
    out<->println(str(x))