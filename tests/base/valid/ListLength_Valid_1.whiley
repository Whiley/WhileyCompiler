import whiley.lang.*:*

void System::main([string] args):
    arr = [1,2,3]
    assert |arr| == 3
    this.out.println(str(arr[0]))
