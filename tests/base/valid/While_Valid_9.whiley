import * from whiley.lang.*

define SIZE as 5

void ::main(System.Console sys):
    components = []
    while |components| < SIZE:
        components = components + [{}]
    sys.out.println(components)
