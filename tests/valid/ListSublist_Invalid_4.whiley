import println from whiley.lang.System

void ::main(System.Console sys):
    list = [1,2,3]
    sublist = list[..2]
    sys.out.println(Any.toString(list))
    sys.out.println(Any.toString(sublist))
