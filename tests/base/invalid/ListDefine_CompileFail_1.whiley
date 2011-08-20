import whiley.lang.*:*

define intset as [int]

void System::main([string] args):
     is = {1,2,3,4}
     this.out.println(str(|il|))
     this.out.println(str(is) ∪ {5})
