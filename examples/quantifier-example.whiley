bool pred({int} xs) ensures !$ || no { z in xs | z < 0 }:
    zs = { z | z in xs, z < 0 }
    return |zs| == 0

int countOver({int} xs, int y) requires pred(xs):
    tmp = { x | x in xs, x > y}
    return |tmp|

int indirectCountOver({int} ys) requires no { y in ys | y < 5 }:
    return countOver(ys,10)

void System::main([string] args):
    c1 = countOver({1,2,3,4},1)
    c2 = countOver({1,2,3,4},3)
    print str(c1)
    print str(c2)
//    int c3 = indirectCountOver({5,123,12465,1123})
//    print str(c3)
