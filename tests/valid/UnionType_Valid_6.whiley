import println from whiley.lang.System

define ur4nat as int
define tur4nat as int
define wur4nat as ur4nat|tur4nat

string f(wur4nat x):
    return Any.toString(x)

void ::main(System.Console sys):
    sys.out.println(f(1))  

