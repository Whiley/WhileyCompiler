void System::main([string] args):
    [int] arr
    if |args| > 0:
        arr = [1,2]
    else:
        arr = [1,2,3]
    assert |arr| == 4 
    print str(arr[0])
