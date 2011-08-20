import whiley.lang.*:*

void System::main([string] args):
    xs = {1,2,3}
    b = 1.0 ∩ xs
    if b:
        this.out.println(str(1))
