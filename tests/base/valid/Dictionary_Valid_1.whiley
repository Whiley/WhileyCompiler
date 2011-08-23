import whiley.lang.*:*

void ::main(System sys,[string] args):
    x = 1
    map = {1->x, 3->2}
    sys.out.println(str(map))
