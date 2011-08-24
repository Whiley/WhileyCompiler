import whiley.lang.*:*

define Link as { int data, LinkedList next }
define LinkedList as null | Link

int sum_1(LinkedList ls):
    if ls is null:
        return 0
    else:
        return ls.data + sum_1(ls.next)

int sum_2(LinkedList ls):
    if ls == null:
        return 0
    else:
        return ls.data + sum_2(ls.next)

int sum_3(LinkedList ls):
    if ls != null:
        return ls.data + sum_3(ls.next)
    else:
        return 0

void ::main(System sys,[string] args):
    ls = { data: 1, next: null}
    ls = { data: 2, next: ls}
    ls = { data: 3, next: ls}
    sys.out.println(str(sum_1(ls)))
    sys.out.println(str(sum_2(ls)))
    sys.out.println(str(sum_3(ls)))
