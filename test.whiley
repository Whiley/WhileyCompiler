void System::main([string] args):
    {int} xs = {0,1,2,3,4,5}
    {int} ys = { x | x in xs, x > |args| }
    print str(ys)
