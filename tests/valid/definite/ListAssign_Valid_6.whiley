void f([int] a) requires |a| > 0:
     a[0] = 5
     print str(a)

void System::main([string] args):
     b = [1,2,3]
     print str(b)
     f(b)
     print str(b)
