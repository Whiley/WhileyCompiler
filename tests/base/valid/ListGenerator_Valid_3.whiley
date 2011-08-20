import whiley.lang.*:*

string f([int] x):
     return str(|x|)

void System::main([string] args):
     arr = []
     this.out.println(f(arr))
