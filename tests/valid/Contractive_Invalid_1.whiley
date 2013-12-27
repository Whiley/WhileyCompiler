import println from whiley.lang.System
    
define Contractive as Contractive|null

Contractive f(Contractive x):
    return x

void ::main(System.Console sys):
    x = f(null)
    sys.out.println(Any.toString(x))
