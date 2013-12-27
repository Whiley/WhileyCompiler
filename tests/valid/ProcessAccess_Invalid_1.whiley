import println from whiley.lang.System

define etype as {int mode, int rest}
define Ptype as ref etype

(int,string) Ptype::get():
    this->mode = 1
    this->rest = 123
    return this->mode,Any.toString(*this)

void ::main(System.Console sys):
    p = new {mode:1,rest:2}
    sys.out.println(Any.toString(*p))
    x,s = p.get()
    sys.out.println(s)
    sys.out.println(Any.toString(*p))
    sys.out.println(Any.toString(x))
