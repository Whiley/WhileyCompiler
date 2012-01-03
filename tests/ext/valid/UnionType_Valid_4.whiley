import * from whiley.lang.*

define ur4nat as int where $ > 0
define tur4nat as int where $ > 10
define wur4nat as ur4nat|tur4nat

string f(wur4nat x):
    return Any.toString(x)

void ::main(System sys,[string] args):
    sys.out.println(f(1))  

