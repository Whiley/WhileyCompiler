define etype as {int mode, int rest}
define Ptype as process etype

int Ptype::get():
    this->mode = 1
    this->rest = 123
    print str(*this)
    return this->mode

void System::main([string] args):
    p = spawn {mode:1,rest:2}
    out->println(str(*p))
    x = p->get()
    out->println(str(*p))
    out->println(str(x))
