import whiley.lang.*:*

define scf3nat as int where $ > 0

int f([scf3nat] xs):
    return |xs| 

void ::main(System sys,[string] args):
    x = [1]
    x[0] = -1
    f(x)
