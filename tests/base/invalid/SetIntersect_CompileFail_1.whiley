import * from whiley.lang.*

void ::main(System sys,[string] args):
    xs = {1,2,3}
    b = 1.0 ∩ xs
    if b:
        sys.out.println(toString(1))
