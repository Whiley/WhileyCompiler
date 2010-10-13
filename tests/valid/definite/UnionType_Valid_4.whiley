define ur4nat as int where $ > 0
define tur4nat as int where $ > 10
define wur4nat as ur4nat|tur4nat

string f(wur4nat x):
    return str(x)

void System::main([string] args):
    out->println(f(1))  

