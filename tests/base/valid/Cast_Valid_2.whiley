import whiley.lang.*:*

define R1 as { real x }

[real] f([int] xs):
    return ([real]) xs

void ::main(System sys,[string] args):
    sys.out.println(str(f([1,2,3])))
    
