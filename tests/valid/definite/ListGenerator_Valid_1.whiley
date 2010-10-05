void f([int] x) requires |x| > 0:
     z = |x|
     out->println(str(z))
     out->println(str(x)[z-1])

void System::main([string] args):
     arr = [1,2,3]
     f(arr)
