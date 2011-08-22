import whiley.lang.*
import whiley.lang.System:System
import whiley.lang.System:println

Type.nat f(int x):
    if x < 0:
        return 0
    else:
        return x

public void System::main([string] args):
    this.out.println(String.str(f(1)))
