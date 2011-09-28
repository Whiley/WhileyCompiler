import * from whiley.lang.*

void ::main(System sys,[string] args):
    arr1 = [1,2,3]
    arr2 = arr1
    arr2[2] = 2
    assert arr2[2] == |arr1|
    debug str(arr1)
    debug str(arr2)
    
