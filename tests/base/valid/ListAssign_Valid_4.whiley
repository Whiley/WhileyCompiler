import whiley.lang.*:*

void System::main([string] args):
     a1 = [[1,2,3],[0]]
     a2 = a1
     a2[0] = [3,4,5]
     
     this.out.println(str(a1[0]))
     this.out.println(str(a1[1]))
     this.out.println(str(a2[0]))
     this.out.println(str(a2[1]))
