function copy(int[] a) -> (int[] b):
    int n = |a|
    return [0; n]

public export method test():
    assume copy([]) == []
    assume copy([1]) == [0]
    assume copy([1,2]) == [0,0]
    assume copy([1,2,3]) == [0,0,0]