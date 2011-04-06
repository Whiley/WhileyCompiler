define etype as {int mode, int rest}
define Ptype as process etype

(int,string) Ptype::get():
    this->mode = 1
    this->rest = 123
    return this->mode,str(*this)

void System::main([string] args):
    p = spawn {mode:1,rest:2}
    out->println(str(*p))
    x,s = p->get()
    out->println(s)
    out->println(str(*p))
    out->println(str(x))
