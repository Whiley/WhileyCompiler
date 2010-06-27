define myProc as process (int x)

int myProc::get():
    return 1

void System::main([string] args):
    if |args| == (spawn (x:1))->get():
        print "GOT HERE"
    else:
        print "MISSED IT"
