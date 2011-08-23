import whiley.lang.*:*

string f([int] x):
     return str(|x|)

void ::main(System sys,[string] args):
     arr = []
     sys.out.println(f(arr))
