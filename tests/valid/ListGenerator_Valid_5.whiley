import println from whiley.lang.System

string f([int] x):
     return Any.toString(|x|)

void ::main(System.Console sys):
     arr = []
     sys.out.println(f(arr))
