import whiley.lang.*
import whiley.lang.System:System
import whiley.lang.System:println

int f(Type.nat x):
    return x-1

public void System::main([string] args):
    this.out.println(String.str(f(1)))
