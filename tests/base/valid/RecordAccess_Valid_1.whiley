import * from whiley.lang.*

define etype as {int mode, ...}
define Ptype as process etype

int Ptype::get():
    this.mode = 1
    return this.mode

void ::main(System sys,[string] args):
    sys.out.println("OK")
