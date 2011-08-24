import whiley.lang.*:*

define sr3nat as int

void ::main(System sys,[string] args):
    x = [1]
    x[0] = 1
    sys.out.println(str(x))
    
