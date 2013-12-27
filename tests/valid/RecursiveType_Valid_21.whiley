import println from whiley.lang.System

define Link as { LinkedList next, int data }
define LinkedList as null|Link

int sum(LinkedList l):
    if l is null:
        return 0
    else:
        return l.data + sum(l.next)

void ::main(System.Console sys):
    l1 = { next: null, data: 1}
    l2 = { next: l1, data: 2}
    l3 = { next: l2, data: 3}
    sys.out.println(Any.toString(sum(l1))) // 1
    sys.out.println(Any.toString(sum(l2))) // 1 + 2 = 3
    sys.out.println(Any.toString(sum(l3))) // 1 + 2 + 3 = 6

