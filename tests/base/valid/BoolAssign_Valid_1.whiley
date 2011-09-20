import * from whiley.lang.*

void ::main(System sys,[string] args):
    x = true
    sys.out.println(str(x))
    x = false
    sys.out.println(str(x))
