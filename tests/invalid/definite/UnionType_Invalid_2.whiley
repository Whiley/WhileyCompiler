define pos as int where $ > 0
define poslist as [pos]
define plt as pos | poslist

define nat as int where $ >= 0
define natlist as [nat]
define nlt as nat | natlist

nlt g(int y) where y >= 0:
    return y

plt f(int x) where x >= 0:
    return g(x)

void System::main([string] args):
    print str(f(0))