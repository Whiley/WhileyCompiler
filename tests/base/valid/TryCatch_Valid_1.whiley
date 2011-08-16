
int f(real x) throws string:
    if x >= 0:
        return 1
    else:
        throw "error"

void System::main([string] args):
    try:
        this.out.println(str(f(1)))
        this.out.println(str(f(0)))
        this.out.println(str(f(-1)))
    catch(string e):
        this.out.println("CAUGHT EXCEPTION: " + e)
    this.out.println("DONE")        