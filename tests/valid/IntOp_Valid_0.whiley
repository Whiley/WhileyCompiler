import println from whiley.lang.System

void ::main(System.Console sys):
     x = 112233445566778899
     sys.out.println(Any.toString(x))
     x = x + 1
     sys.out.println(Any.toString(x))
     x = x - 556
     sys.out.println(Any.toString(x))
     x = x * 123
     sys.out.println(Any.toString(x))
     x = x / 2
     sys.out.println(Any.toString(x))
