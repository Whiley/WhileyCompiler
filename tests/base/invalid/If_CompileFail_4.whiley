import whiley.lang.*:*

int f(int x):
    if(x < 10):
        return 1
    else if(x > 10):
        return 2

void System::main([string] args):
    this.out.println(str(f(1)))
    this.out.println(str(f(10)))
    this.out.println(str(f(11)))
    this.out.println(str(f(1212)))
    this.out.println(str(f()-1212))
