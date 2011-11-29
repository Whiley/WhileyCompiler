import * from whiley.lang.*

define etype as {int mode, ? rest}
define Ptype as process etype

int Ptype::get():
    this.op = 1
    return this.mode

void ::main(System sys,[string] args):
    sys.out.println("OK")
