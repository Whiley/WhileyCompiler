import println from whiley.lang.System

define TYPE as [CONSTANT]
define CONSTANT as {(V1 is TYPE), (V2 is TYPE)}

define V1 as [false]
define V2 as []

void ::main(System.Console sys):
    sys.out.println(CONSTANT)
