ur4nat as int where $ > 0
tur4nat as int where $ > 10
define wur4nat as ur4nat|tur4nat

void f(wur4nat x):
    print str(x)

void System::main([string] args):
    f(1)

