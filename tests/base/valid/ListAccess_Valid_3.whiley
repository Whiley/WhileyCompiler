import whiley.lang.*:*

string f([int] x):
     return str(|x|)

void ::main(System sys,[string] args):
     arr = [[1,2,3]]
     sys.out.println(f(arr[0]))
