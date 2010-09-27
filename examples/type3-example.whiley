define lilset as {1,2,3}

void System::main([string] args):
    xs = 3
    ys = {4}
    ys = ys ∪ lilset
    
    print str(xs)
    print str(ys)
