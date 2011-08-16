[int] f():
    return [1,2]

void System::main([string] args):
     a1 = f()
     a2 = f()
     a2[0] = 0
     
     this.out.println(str(a1[0]))
     this.out.println(str(a1[1]))
     this.out.println(str(a2[0]))
     this.out.println(str(a2[1]))
