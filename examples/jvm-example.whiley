import * from whiley.lang.*

define Wacky as process {int d}

void Wacky::doSomething():
    extern jvm:
        invokestatic Helper.doStuff:()V;

void ::main(System sys, [string] args):
    wp = spawn {d:1}
    wp.doSomething()
