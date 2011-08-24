import whiley.lang.*:*

define scf4set as {int} where |$| > 0

int f(scf4set x):
    return 1

void ::main(System sys,[string] args):
    x = {}
    f(x)
    
