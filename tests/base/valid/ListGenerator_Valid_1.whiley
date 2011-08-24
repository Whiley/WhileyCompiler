import whiley.lang.*:*

void System::f([int] x):
    z = |x|
    sys.out.println(str(z))
    sys.out.println(str(x[z-1]))

void ::main(System sys,[string] args):
     arr = [1,2,3]
     // following line should block
     this.f(arr)
