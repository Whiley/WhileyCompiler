import * from whiley.lang.*

void ::main(System sys,[string] args):
    list = [1,2,3]
    sublist = list[2..0]
    sys.out.println(str(list))
    sys.out.println(str(sublist))
