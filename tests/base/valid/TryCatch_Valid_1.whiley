
int f(real x) throws string:
    if x >= 0:
        return 1
    else:
        throw "error"

void System::main([string] args):
    try:
        out<->println(str(f(1)))
        out<->println(str(f(0)))
        out<->println(str(f(-1)))
    catch(string e):
        out<->println("CAUGHT EXCEPTION: " + e)
    out<->println("DONE")        