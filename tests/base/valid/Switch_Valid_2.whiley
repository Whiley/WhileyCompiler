import whiley.lang.*:*

void System::main([string] args):
    x = 1
    switch x:
        case 1:
            this.out.println("CASE 1")
            return
        case 2:
            this.out.println("CASE 2")
            return
    this.out.println("DEFAULT CASE")
