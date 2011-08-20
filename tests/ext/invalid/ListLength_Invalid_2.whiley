import whiley.lang.*:*

void System::main([string] args):
    if |args| > 0:
        arr = [1,2,4]
    else:
        arr = [1,2,3]
    assert arr[0] < |arr|
    assert arr[1] < |arr|
    assert arr[2] != |arr|
    debug str(arr[0])
