import whiley.lang.*:*

define wmccf7tup as {int x, int y}

wmccf7tup f(System x, int x):
    return {x:1,y:x.get()}

int System::get():
    return 1

void ::main(System sys,[string] args):
    sys.out.println(str(f(this),1))
