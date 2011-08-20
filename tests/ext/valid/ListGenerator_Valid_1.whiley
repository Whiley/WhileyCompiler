import whiley.lang.*:*

void f([int] x) requires |x| > 0:
    z = |x|
    debug str(z) + "\n"
    debug str(x[z-1]) + "\n"

void System::main([string] args):
     arr = [1,2,3]
     f(arr)
