import * from whiley.lang.*

void f([int] xs) requires xs != []:
    debug str(xs)

void ::main(System sys,[string] args):
    f([1,4])
    f([])
