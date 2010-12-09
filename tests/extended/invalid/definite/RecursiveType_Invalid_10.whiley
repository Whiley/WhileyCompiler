define LinkedList as int | {LinkedList next, int data}

define posLink as {posList next, nat data}
define posList as int | posLink

nat sum(LinkedList list):
    if list ~= int:
        return 0
    else:
        return list.data + sum(list.next)

void System::main([string] args):
    l = { next:1, data:1 }
    print str(sum(l))
    l = { next:l, data:-2 }
    print str(sum(l))    
