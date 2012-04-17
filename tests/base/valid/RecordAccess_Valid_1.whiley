import println from whiley.lang.System

define etype as {int mode, ...}
define Ptype as ref etype

int Ptype::get():
    this->mode = 1
    return this->mode

void ::main(System.Console sys):
    sys.out.println("OK")
