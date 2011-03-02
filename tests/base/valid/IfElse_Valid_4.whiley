define Link as { int data, LinkedList next }
define LinkedList as null | Link

int sum_1(LinkedList ls):
    if ls ~= null:
        return 0
    else:
        return 1 + sum_1(ls.next)

int sum_2(LinkedList ls):
    if ls == null:
        return 0
    else:
        return 1 + sum_2(ls.next)

int sum_3(LinkedList ls):
    if ls != null:
        return 1 + sum_3(ls.next)
    else:
        return null

void System::main([string] args):
    ls = { val: 1, next: null}
    ls = { val: 2, next: ls}
    ls = { val: 3, next: ls}
    print str(sum_1(ls))
    print str(sum_2(ls))
    print str(sum_3(ls))