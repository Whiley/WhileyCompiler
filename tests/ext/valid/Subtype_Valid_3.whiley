import whiley.lang.*:*

define sr3nat as int where $ > 0

void ::main(System sys,[string] args):
    x = [1]
    x[0] = 1
    sys.out.println(str(x))
    
