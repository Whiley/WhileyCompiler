import * from whiley.lang.*

string f([[int]] x) requires |x| > 0:
    if(|x[0]| > 2):
        return toString(x[0][1])
    else:
        return ""


void ::main(System sys,[string] args):
     arr = [[1,2,3],[1]]
     sys.out.println(f(arr))
