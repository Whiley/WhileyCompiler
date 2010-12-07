define Link as { LinkedList next, int data }
define LinkedList as null|Link

int sum(LinkedList l):
    if l ~= null:
        return 0
    else:
        return l.data + sum(l.next)

void System::main([string] args):
    l1 = { next: null, data: 1}
    x = sum(l1)
    out->println(str(x))
