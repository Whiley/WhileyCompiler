define nat as int requires $ >= 0
define natlist as [nat]
define nlt as nat | natlist

nlt g(int y):
    return y

void System::main([string] args):
    g(-1)
