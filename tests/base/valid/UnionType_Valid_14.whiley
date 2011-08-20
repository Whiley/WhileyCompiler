import whiley.lang.*:*

define nlist as nat|[int]

nlist f(int x):
    if x <= 0:
        return 0
    else:
        return f(x-1)

void System::main([string] args):
    x = f(2)    
    this.out.println(str(x))
