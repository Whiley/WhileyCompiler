define Link as { bool val, LinkedList next }
define LinkedList as null | Link

int sum(LinkedList ls):
    if ls ~= null:
        return 0
    else if !ls.val:
        return sum(ls.next)
    else:
        return 1 + sum(ls.next)

void System::main([string] args):
    ls = { val: true, next: null}
    out<->println(str(sum(ls)))
    ls = { val: true, next: ls}
    out<->println(str(sum(ls)))
    ls = { val: false, next: ls}
    out<->println(str(sum(ls)))
