import whiley.lang.*:*

define sr4set as {int} where |$| > 0

void ::main(System sys,[string] args):
    x = {1}
    sys.out.println(str(x))
    
