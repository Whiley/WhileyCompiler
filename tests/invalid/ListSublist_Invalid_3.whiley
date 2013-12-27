import * from whiley.lang.*

void ::main(System.Console sys):
    list = [1,2,3]
    sublist = list[2..x]
    sys.out.println(Any.toString(list))
    sys.out.println(Any.toString(sublist))
