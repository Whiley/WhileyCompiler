import whiley.lang.*:*
    
define Contractive as Contractive|null

Contractive f(Contractive x):
    return x

void ::main(System sys, [string] args):
    x = f(null)
    sys.out.println(str(x))
