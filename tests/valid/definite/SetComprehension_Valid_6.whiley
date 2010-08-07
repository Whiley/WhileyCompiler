{int} f({int} xs) requires no { x ∈ xs | x < 0 }, ensures no { y ∈ $ | y > 0 }:
    return { -x | x ∈ xs } 

void System::main([string] args):
    print str(f({1,2,3,4}))
