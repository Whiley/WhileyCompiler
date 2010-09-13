define LinkedList as int | {LinkedList next, int data}

define posLink as {posList next, nat data}
define posList as int | posLink

posList f(LinkedList list):
    return list

void System::main([string] args):
    LinkedList l = { next:{ next:1, data:-1 }, data:1 }
    print str(f(l))
