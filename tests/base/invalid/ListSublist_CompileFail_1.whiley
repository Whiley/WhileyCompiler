import * from whiley.lang.*

void ::main(System sys,[string] args):
    end = 1.2344
    list = [1,2,3]
    sublist = list[-1..end]
    sys.out.println(toString(list))
    sys.out.println(toString(sublist))
