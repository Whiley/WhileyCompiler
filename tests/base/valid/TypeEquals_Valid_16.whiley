import whiley.lang.*:*

string f(int|null x):
    if x is null:
        return "GOT NULL"
    else:
        return "GOT INT"

void ::main(System sys,[string] args):
    x = null
    sys.out.println(f(x))
    sys.out.println(f(1))
