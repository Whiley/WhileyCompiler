import whiley.lang.*:*

define etype as {int mode, int rest}
define Ptype as process etype

(int,string) Ptype::get():
    this.mode = 1
    this.rest = 123
    return this.mode,str(*this)

void ::main(System sys,[string] args):
    p = spawn {mode:1,rest:2}
    sys.out.println(str(*p))
    x,s = p.get()
    sys.out.println(s)
    sys.out.println(str(*p))
    sys.out.println(str(x))
