import * from whiley.lang.*

void ::main(System sys,[string] args):
    arr1 = [1,2,4]
    arr2 = arr1
    arr2[2] = 3
    assert arr2[2] == |arr1|
    sys.out.println(str(arr1))
    sys.out.println(str(arr2))
