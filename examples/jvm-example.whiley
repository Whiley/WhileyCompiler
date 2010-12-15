define Wacky as process {int d}

void Wacky::doSomething():
    extern jvm:
        invokestatic Helper.doStuff:()V;

void System::main([string] args):
    wp = spawn {d:1}
    wp->doSomething()
