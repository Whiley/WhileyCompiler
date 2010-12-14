define tac2ta as {int f1, int f2} where f1 < f2
define tac2tb as {int f1, int f2} where (f1+1) < f2

tac2tb f(tac2ta x):
    return {f1: x.f1-1, f2: x.f2}

void System::main([string] args):
    x = {f1:2,f2:3}
    out->println(str(x))
    x.f1 = 1
    y = f(x)
    out->println(str(y))
