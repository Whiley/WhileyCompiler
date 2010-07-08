define pos as int where $ > 0
define poslist as [pos]
define plt as pos | poslist

define nat as int where $ >= 0
define natlist as [nat]
define nlt as nat | natlist

nlt g(int y) requires y > 2:
    return y

plt f(int x) requires x > 1:
    return g(x+1)

void System::main([string] args):
    print str(f(2))