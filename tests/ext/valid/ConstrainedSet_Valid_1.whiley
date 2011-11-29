import * from whiley.lang.*

{int8} f(int x) requires x == 0 || x == 169:
    return {x}

void ::main(System sys,[string] args):
    bytes = f(0)
    sys.out.println(toString(bytes))

