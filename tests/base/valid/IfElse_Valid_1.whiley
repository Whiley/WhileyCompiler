import whiley.lang.*:*

string f(int x):
    if(x < 10):
        return "LESS THAN"
    else if(x > 10):
        return "GREATER THAN"
    else:
        return "EQUALS"

void System::main([string] args):
    this.out.println(f(1))
    this.out.println(f(10))
    this.out.println(f(11))
    this.out.println(f(1212))
    this.out.println(f(-1212))