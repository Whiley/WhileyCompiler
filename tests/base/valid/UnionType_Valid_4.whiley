import * from whiley.lang.*

define ur4nat as int
define tur4nat as int
define wur4nat as ur4nat|tur4nat

string f(wur4nat x):
    return toString(x)

void ::main(System sys,[string] args):
    sys.out.println(f(1))  

