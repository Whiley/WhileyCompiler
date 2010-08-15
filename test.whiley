void System::main([string] args):
    {int} xs = {1,2,3}
    xs = {x | x in xs, x > |args|}
    print str(xs)
