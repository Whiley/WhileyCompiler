import println from whiley.lang.System

define Link as { bool val, LinkedList next }
define LinkedList as null | Link

int sum(LinkedList ls):
    if ls is null:
        return 0
    else if !ls.val:
        return sum(ls.next)
    else:
        return 1 + sum(ls.next)

void ::main(System.Console sys):
    ls = { val: true, next: null}
    sys.out.println(Any.toString(sum(ls)))
    ls = { val: true, next: ls}
    sys.out.println(Any.toString(sum(ls)))
    ls = { val: false, next: ls}
    sys.out.println(Any.toString(sum(ls)))
