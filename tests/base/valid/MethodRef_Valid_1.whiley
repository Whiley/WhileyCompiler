define MyProc as process {
    int position
}

define MyMeth as int(MyProc)::(int)

int MyProc::read(int x):
    return x + 123

int System::test(MyMeth m, MyProc proc):
    return proc.m(1)

void System::main([string] args):
    p = spawn { position: 0 }
    r = this.test(&read,p)
    out.println(str(r))


