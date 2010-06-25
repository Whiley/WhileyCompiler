define int where $ > 0 as ur4nat
define int where $ > 10 as tur4nat
define ur4nat|tur4nat as wur4nat

void f(wur4nat x):
    print str(x)

void System::main([string] args):
    f(1)

