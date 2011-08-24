import whiley.lang.*:*

// this is a comment!
define IntReal as int | real

int f(int x):
    return x

void ::main(System sys,[string] args):
    if |args| > 0:
        x = 1.23
    else:
        x = 1
    sys.out.println(str(x))
    f(x)

