import * from whiley.lang.*

// this was taken from jasm
int constantPool():
    return 12478623847120981

void ::main(System sys, [string] args):
    constantPool = constantPool()
    sys.out.println("GOT: " + String.toString(constantPool))
