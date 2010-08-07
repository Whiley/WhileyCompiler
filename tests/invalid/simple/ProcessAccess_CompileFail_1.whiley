define (int mode, ? rest) as etype
define Ptype as process etype

int Ptype::get():
    this->op = 1
    return this->mode

void System::main([string] args):
    print "OK"
