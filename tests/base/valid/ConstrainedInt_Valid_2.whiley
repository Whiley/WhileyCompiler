// this is a comment!
define cr2num as {1,2,3,4}

string f(cr2num x):
    y = x
    return str(y)

void System::main([string] args):
    out.println(f(3))
