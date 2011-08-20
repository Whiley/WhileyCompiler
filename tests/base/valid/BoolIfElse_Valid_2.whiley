import whiley.lang.*:*

define Link as { bool val, LinkedList next }
define LinkedList as null | Link

int sum(LinkedList ls):
    if ls is null:
        return 0
    else if !ls.val:
        return sum(ls.next)
    else:
        return 1 + sum(ls.next)

void System::main([string] args):
    ls = { val: true, next: null}
    this.out.println(str(sum(ls)))
    ls = { val: true, next: ls}
    this.out.println(str(sum(ls)))
    ls = { val: false, next: ls}
    this.out.println(str(sum(ls)))
