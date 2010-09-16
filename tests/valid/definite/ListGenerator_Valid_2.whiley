void f([[int]] x) requires |x| > 0:
    if(|x[0]| > 2):
        print str(x[0][1])


void System::main([string] args):
     arr = [[1,2,3],[1]]
     f(arr)
