import * from whiley.lang.*

{real} f([real] x):
    return x

void ::main(System sys,[string] args):
    x = f([2.2,3.3])
    sys.out.println(str(x))