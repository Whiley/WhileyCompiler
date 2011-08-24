import whiley.lang.*:*

void System::f(int x):
    this.out.println(str(x))

void ::main(System sys,[string] args):
    sys.f(1)
    sys.out.print("")
