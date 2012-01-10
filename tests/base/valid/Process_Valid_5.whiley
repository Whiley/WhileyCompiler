import * from whiley.lang.*


void ::main(System.Console sys,[string] args):
    // the should override the implicit field 
    // scope of the field "out" in System.
    out = 1
    sys.out.println(Any.toString(out))
