import whiley.lang.*:*

[int] f([int] ls):
    return ls

void System::main([string] args):
    xs = [1,2]
    xs[0] = 1.23
    f(xs) // should fail
