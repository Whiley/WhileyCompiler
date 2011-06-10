define expr as {int}|bool

string f(expr e):
    if e ~= {int}:
        return "GOT {INT}"
    else if e ~= bool:
        return "GOT BOOL"
    else:
        return "GOT SOMETHING ELSE?"

void System::main([string] args):
    e = true
    out.println(f(e))
    e = {1,2,3,4}
    out.println(f(e))
 
