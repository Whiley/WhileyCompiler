import * from whiley.lang.*

{int->int} f(int x):
    return {1->x, 3->2}

int get(int i, {int->int} map):
    return map[i]

void ::main(System sys,[string] args):
    sys.out.println(toString(get(1,f(1))))
    sys.out.println(toString(get(1,f(2))))
    sys.out.println(toString(get(1,f(3))))
    sys.out.println(toString(get(3,f(3))))
