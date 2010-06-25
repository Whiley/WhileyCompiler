define int where $ > 0 as urf1nat
define int where $ > 10 as turf1nat
define urf1nat|turf1nat as wurf1nat

void f(wurf1nat x):
    print str(x)

void g(int x):
    f(x)

void System::main([string] args):
    g(1)
    g(-1)

