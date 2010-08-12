void System::main([string] args):
    {int} xs = {|args|}
    if some { x in xs | x == 0 }:
        print "SOME ZERO"
