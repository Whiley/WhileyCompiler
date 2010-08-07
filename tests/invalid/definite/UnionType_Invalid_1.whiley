define urf1nat as int where $ > 0
define turf1nat as int where $ > 10
define wurf1nat as urf1nat|turf1nat

void f(wurf1nat x):
    print str(x)

void g(int x):
    f(x)

void System::main([string] args):
    g(1)
    g(-1)

