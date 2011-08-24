import whiley.lang.*:*

define posints as {int}

string f(posints x):
    return str(x)

void ::main(System sys,[string] args):
    xs = {1,2,3}
    sys.out.println(f(xs))
