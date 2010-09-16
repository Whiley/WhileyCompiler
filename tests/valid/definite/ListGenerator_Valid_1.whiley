void f([int] x) requires |x| > 0:
     z = |x|
     print str(z)
     print str(x[z-1])

void System::main([string] args):
     arr = [1,2,3]
     f(arr)
