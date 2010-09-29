// Define the set of positive numbers
define pset as {int} where no { y in $ | y <= 0 }

// Define the set of negative numbers
define nset as {int} where no { y in $ | y >= 0 }

// negate accepts a set of positive numbers, and negates them all.
nset negate(pset xs):
    r = {}
    for x in xs where no { y in r | y >= 0 }:
        r = r + {-x}
    return r

// The broken method forces a symbolic proof
//
//nset broken(pset xs):
//    return negate(xs ∪ {-1})

void System::main([string] args):
    xs = negate({1,2,3,4})
    print str(xs)
