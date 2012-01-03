import * from whiley.lang.*

string f([[int]] x):
    if(|x[0]| > 2):
        return Any.toString(x[0][1])
    else:
        return ""


void ::main(System sys,[string] args):
     arr = [[1,2,3],[1]]
     sys.out.println(f(arr))
