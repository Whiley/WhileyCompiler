import * from whiley.lang.*
import * from whiley.lang.*
import * from whiley.lang.*

int f(Type.nat x):
    return x-1

public void ::main(System sys,[string] args):
    sys.out.println(String.toString(f(1)))
