define fr6nat as int where $ >= 0

{fr6nat} g({fr6nat} xs):
    return { y | y in xs, y > 1 }

string f({int} x):
    return str(x)

void System::main([string] args):
    ys = {1,2,3}
    out<->println(f(g(ys)))
