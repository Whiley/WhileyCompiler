import whiley.lang.*:*

// this is a comment!
define irf3nat as int where $ < 10
define pirf3nat as irf3nat where $ > 0

pirf3nat f(int x):
    return x

void ::main(System sys,[string] args):
    debug str(f(11))
