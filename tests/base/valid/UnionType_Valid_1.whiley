import whiley.lang.*:*

void System::main([string] args):    
    if |args| == 1:
        x = 1
    else:
        x = [1,2,3]
    this.out.println(str(x))

