import whiley.lang.*:*

int f(System x, int x):
    return x.get()

int System::get():
    return 1

void ::main(System sys,[string] args):
    sys.out.println(str(f(this),1))
