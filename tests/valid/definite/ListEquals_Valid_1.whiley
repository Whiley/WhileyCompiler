void f([int] xs, [real] ys):
    print str(xs)
    print str(ys)
    if xs == ys:
        print "EQUAL"
    else:
        print "NOT EQUAL"

void System::main([string] args):
    f([1,4],[1.0,4.0])
    f([1,4],[1.0,4.2])
    f([],[])
