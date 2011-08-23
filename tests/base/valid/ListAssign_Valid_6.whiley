import whiley.lang.*:*

string f([int] a):
     a[0] = 5
     return str(a)

void ::main(System sys,[string] args):
     b = [1,2,3]
     sys.out.println(str(b))
     sys.out.println(f(b))
     sys.out.println(str(b))
