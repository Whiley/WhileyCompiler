define DL1 as {int->int}
define DL2 as {int->real}

DL2 update(DL1 ls):
    ls[0] = 1.234
    return ls

void System::main([string] args):
    x = {0->1, 1->2}
    x = update(x)
    this.out.println(str(x))
