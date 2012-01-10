import * from whiley.lang.*

define MyProc as process {
    int position
}

define MyMeth as int(MyProc)::(int)

int MyProc::read(int x):
    return x + 123

int ::test(MyMeth m, MyProc proc):
    return proc.m(1)

void ::main(System.Console sys,[string] args):
    p = spawn { position: 0 }
    r = test(&read,p)
    sys.out.println(Any.toString(r))


