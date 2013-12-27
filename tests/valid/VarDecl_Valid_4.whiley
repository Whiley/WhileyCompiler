import println from whiley.lang.System

// this was taken from jasm
int constantPool():
    return 12478623847120981

void ::main(System.Console sys):
    constantPool = constantPool()
    sys.out.println("GOT: " + Any.toString(constantPool))
