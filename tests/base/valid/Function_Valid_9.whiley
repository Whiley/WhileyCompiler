import whiley.lang.*:*

string f(int x):
    return "F(INT)"

string f(real y):
    return "F(REAL)"

string f([int] xs):
    return "F([int])"

string f({int} xs):
    return "F({int})"


void System::main([string] args):
    this.out.println(f(1.234))
    this.out.println(f(1))
    this.out.println(f([1,2,3]))
    this.out.println(f({1,2,3}))