import println from whiley.lang.System

string f([int] x):
     return Any.toString(|x|)

void ::main(System.Console sys):
     arr = [[1,2,3]]
     sys.out.println(f(arr[0]))
