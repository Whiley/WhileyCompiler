import whiley.lang.*:*

define nat as int where $ >= 0
define natlist as [nat]
define nlt as nat | natlist

nlt g(int y):
    return y

void System::main([string] args):
    g(-1)
