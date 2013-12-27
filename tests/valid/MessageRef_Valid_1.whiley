import println from whiley.lang.System

define MyProc as ref {
    int position
}

define MyMeth as int(MyProc)::(int)

int MyProc::read(int x):
    return x + 123

int ::test(MyMeth m, MyProc proc):
    return proc.m(1)

void ::main(System.Console sys):
    p = new { position: 0 }
    r = test(&read,p)
    sys.out.println(Any.toString(r))


