bool pred({int} xs) where !$ || no { z in xs | z < 0 }:
    {int} zs = { z | z in xs, z < 0 }
    return |zs| == 0

int countOver({int} xs, int y) where pred(xs):
    {int} tmp = { x | x in xs, x > y}
    return |tmp|

void System::main([string] args):
    int c1 = countOver({1,2,3,4},1)
    int c2 = countOver({1,2,3,4},3)
    print str(c1)
    print str(c2)
