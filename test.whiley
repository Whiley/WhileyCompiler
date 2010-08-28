define pset as {int} where no { x in $ | x < 0 }

void System::main([string] args):
    pset ys = {1,2,3,4}
    print str(ys)
