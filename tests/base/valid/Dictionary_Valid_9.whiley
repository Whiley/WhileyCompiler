define DL1 as {int->int}
define DL2 as {real->int}

DL2 update(DL1 ls):
    ls[1.2] = 1
    return ls

void System::main([string] args):
    x = {0->1, 1->2}
    x = update(x)
    out.println(str(x))
