import whiley.lang.*:*

define point as {int x, int y}

void ::main(System sys,[string] args):
    p = {x:1,y:1}
    sys.out.println(str(p))
