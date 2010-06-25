define int where $ >= 0 as fr6nat

{fr6nat} g({fr6nat} xs):
    return { y | y in xs, y > 1 }

void f({int} x):
    print str(x)

void System::main([string] args):
    {fr6nat} ys = {1,2,3}
    f(g(ys))
