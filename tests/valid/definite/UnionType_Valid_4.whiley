define ur4nat as int requires $ > 0
define tur4nat as int requires $ > 10
define wur4nat as ur4nat|tur4nat

void f(wur4nat x):
    print str(x)

void System::main([string] args):
    f(1)

