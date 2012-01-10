import * from whiley.lang.*

void ::main(System.Console sys,[string] args):
    list = [1,2,3]
    sublist = list[1..4]
    debug Any.toString(list)
    debug Any.toString(sublist)
