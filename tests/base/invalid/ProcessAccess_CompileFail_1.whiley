import * from whiley.lang.*

define etype as {int mode, ? rest}
define Ptype as ref etype

int Ptype::get():
    this.op = 1
    return this.mode

void ::main(System.Console sys):
    sys.out.println("OK")
