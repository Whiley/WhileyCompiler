

public export method test() :
    int[][] a1 = [[1, 2, 3], [0]]
    int[][] a2 = a1
    a2[0] = [3, 4, 5]
    assert a1[0] == [1,2,3]
    assert a1[1] == [0]    
    assert a2[0] == [3,4,5]
    assert a2[1] == [0]    
