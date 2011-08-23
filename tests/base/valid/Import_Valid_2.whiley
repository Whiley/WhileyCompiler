import whiley.lang.*
import whiley.lang.System:System
import whiley.lang.System:println

int f(Type.nat x):
    return x-1

public void ::main(System sys,[string] args):
    sys.out.println(String.str(f(1)))
