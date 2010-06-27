define lilset as {1,2,3}

void System::main([string] args):
    lilset xs = 3
    {int} ys = {4}
    ys = ys ∪ lilset
    
    print str(xs)
    print str(ys)