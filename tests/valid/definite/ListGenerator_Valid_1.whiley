void f([int] x) where |x| > 0:
     int z
     z = |x|
     print str(z)
     print str(x[z-1])

void System::main([string] args):
     [int] arr
     arr = [1,2,3]
     f(arr)
