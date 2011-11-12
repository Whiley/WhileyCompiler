import * from whiley.lang.*

void ::main(System sys,[string] args):
    list = [1,2,3]
    sublist = list[2..x]
    sys.out.println(toString(list))
    sys.out.println(toString(sublist))
