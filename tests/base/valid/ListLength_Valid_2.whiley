import println from whiley.lang.System

void ::main(System.Console sys):
     arr = [1,2,3]
     assert arr[0] < |arr|
     assert arr[1] < |arr|
     assert arr[2] == |arr|
     sys.out.println(Any.toString(arr[0]))
