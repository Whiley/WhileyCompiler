import whiley.lang.*:*

{int->int} f(int x):
    return {1->x, 3->2}

void ::main(System sys,[string] args):
    sys.out.println(str(f(1)))
    sys.out.println(str(f(2)))
    sys.out.println(str(f(3)))
