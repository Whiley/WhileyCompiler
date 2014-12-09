import * from whiley.lang.*

type etype is {int mode, ...}

type Ptype is &etype

method get(Ptype this) -> int:
    this.op = 1
    return this.mode

method main(System.Console sys) -> void:
    sys.out.println("OK")
