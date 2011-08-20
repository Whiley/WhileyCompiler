import whiley.lang.*:*

string f([bool] x) requires |x| > 0 && x[0]:
    return str(x)

void System::main([string] args):
    this.out.println(f([true]))
    this.out.println(f([true,false]))
    this.out.println(f([true,false,true]))
    
