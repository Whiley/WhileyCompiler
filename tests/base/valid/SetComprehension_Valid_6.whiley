{int} f({int} xs):
    return { -x | x ∈ xs } 

void System::main([string] args):
    this.out.println(str(f({1,2,3,4})))
