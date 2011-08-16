string f(bool b):
    if(b):
        return "TRUE"
    else:
        return "FALSE"

void System::main([string] args):
    this.out.println(f(true))
    this.out.println(f(false))
