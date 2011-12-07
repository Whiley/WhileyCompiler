import * from whiley.lang.*

void ::main(System sys,[string] args):
    list = [1,2,3]
    sublist = list[..2]
    sys.out.println(Any.toString(list))
    sys.out.println(Any.toString(sublist))
