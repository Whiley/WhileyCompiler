import whiley.lang.*:*

define fr3nat as int where $ >= 0

string f(int x):
    return str(x)

void System::main([string] args):
    y = 1
    this.out.println(f(y))
