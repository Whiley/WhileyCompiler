import * from whiley.lang.*

void ::main(System.Console sys):
    xs = {1,2,3}
    if 1.23 ∈ xs:
        sys.out.println(Any.toString(1))
