import * from whiley.lang.*

void ::main(System.Console sys):
    if |args| > 0:
        arr = [1,2,4]
    else:
        arr = [1,2,3]
    assert arr[0] < |arr|
    assert arr[1] < |arr|
    assert arr[2] != |arr|
    debug Any.toString(arr[0])
