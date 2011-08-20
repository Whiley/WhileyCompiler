import whiley.lang.*:*

string f([int] x):
     return str(|x|)

void System::main([string] args):
     arr = [[1,2,3]]
     this.out.println(f(arr[0]))
