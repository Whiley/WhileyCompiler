define process (int d) as Wacky

void Wacky::doSomething():
    extern jvm:
        invokestatic Helper.doStuff:()V;

void System::main([string] args):
    Wacky wp = spawn (d:1)
    wp->doSomething()