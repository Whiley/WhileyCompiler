void System::main([string] args):
    arr1 = [1,2,4]
    arr2 = arr1
    arr2[2] = 3
    assert arr2[2] == |arr1|
    this.out.println(str(arr1))
    this.out.println(str(arr2))
