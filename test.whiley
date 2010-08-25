define pos as int where $ > 0
define neg as int where $ < 0

define intlist as pos|neg|[int]

int f(intlist x):
    if x ~= int:
        return x
    return 1 // unreachable

void System::main([string] args):
    print str(f([1,2,3]))
    print str(f(2))
