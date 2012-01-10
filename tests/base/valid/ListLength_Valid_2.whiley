import * from whiley.lang.*

void ::main(System.Console sys,[string] args):
     arr = [1,2,3]
     assert arr[0] < |arr|
     assert arr[1] < |arr|
     assert arr[2] == |arr|
     sys.out.println(Any.toString(arr[0]))
