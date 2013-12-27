

void ::main(System.Console sys):
    if |sys.args| > 0:
        arr = [1,2]
    else:
        arr = [1,2,3]
    assert |arr| == 4 
    debug Any.toString(arr[0])
