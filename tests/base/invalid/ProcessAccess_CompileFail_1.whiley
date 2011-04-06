define etype as {int mode, ? rest}
define Ptype as process etype

int Ptype::get():
    this->op = 1
    return this->mode

void System::main([string] args):
    out->println("OK")
