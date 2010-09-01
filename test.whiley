define pset as {int} where no { x in $ | x < 0 }

void f(pset xs):
    print "HELLO"

void g({int} set):    
    f({x | x in set, x >= 0})

void System::main([string] args):
    g({1,2,3,4})
