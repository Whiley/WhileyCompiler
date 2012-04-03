import println from whiley.lang.System

string f([[int]] x) requires |x| > 0:
    if(|x[0]| > 2):
        return Any.toString(x[0][1])
    else:
        return ""


void ::main(System.Console sys):
     arr = [[1,2,3],[1]]
     sys.out.println(f(arr))
