string f([[int]] x) requires |x| > 0:
    if(|x[0]| > 2):
        return str(x[0][1])
    else:
        return ""


void System::main([string] args):
     arr = [[1,2,3],[1]]
     this.out.println(f(arr))
