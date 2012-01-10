import * from whiley.lang.*

string f([int] x):
     return Any.toString(|x|)

void ::main(System.Console sys,[string] args):
     arr = []
     sys.out.println(f(arr))
