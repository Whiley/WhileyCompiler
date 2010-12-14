define pos as int
define neg as int

define intlist as pos|neg|[int]

int f(intlist x):
    if x ~= int:
        return x
    return 1 

void System::main([string] args):
    x = f([1,2,3])
    out->println(str(x))
    x = f(123)
    out->println(str(x))

