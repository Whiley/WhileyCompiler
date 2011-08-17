define nat as int where $ >= 0
define expr as nat | {int op, expr left, expr right}

void System::main([string] args):
    e = 14897
    out.println(str(e))
