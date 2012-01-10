import * from whiley.lang.*

[int] f(System x, int x):
    return [1,2,3,x.get()]

int System::get():
    return 1

void ::main(System.Console sys,[string] args):
    sys.out.println(Any.toString(f(this),1))
