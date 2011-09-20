import * from whiley.lang.*

void ::main(System sys,[string] args):
     x = 112233445566778899
     sys.out.println(str(x))
     x = x + 1
     sys.out.println(str(x))
     x = x - 556
     sys.out.println(str(x))
     x = x * 123
     sys.out.println(str(x))
     x = x / 2
     sys.out.println(str(x))
