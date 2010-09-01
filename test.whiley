int countOver({int} xs):
    {int} tmp = { x | x in xs, x > 1}
    return |tmp|

void System::main([string] args):
    print str(countOver({0,1,2,3}))
