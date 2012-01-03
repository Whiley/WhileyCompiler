import * from whiley.lang.*

string f([int] x):
     return Any.toString(|x|)

void ::main(System sys,[string] args):
     arr = []
     sys.out.println(f(arr))
