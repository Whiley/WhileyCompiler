import * from whiley.lang.*

void ::main(System sys,[string] args):
    arr = [1,2,3]
    assert |arr| == 3
    sys.out.println(toString(arr[0]))
