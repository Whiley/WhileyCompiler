import * from whiley.lang.*

define intset as [int]

void ::main(System sys,[string] args):
     is = {1,2,3,4}
     sys.out.println(Any.toString(|il|))
     sys.out.println(Any.toString(is) ∪ {5})
