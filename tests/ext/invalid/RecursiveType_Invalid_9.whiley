import * from whiley.lang.*

define LinkedList as int | {LinkedList next, int data}

define posLink as {posList next, nat data}
define posList as int | posLink

posList f(LinkedList list):
    return list

void ::main(System.Console sys):
    l = { next:{ next:1, data:-1 }, data:1 }
    debug Any.toString(f(l))
