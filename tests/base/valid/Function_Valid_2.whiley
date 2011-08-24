import whiley.lang.*:*

define fr2nat as int

string f(fr2nat x):
    return str(x)

void ::main(System sys,[string] args):
    y = 1
    sys.out.println(f(y))
