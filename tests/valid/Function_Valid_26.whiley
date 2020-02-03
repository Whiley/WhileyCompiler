function id(int x) -> int:
    return x

function id(bool x) -> bool:
    return x

function id(int[] x) -> int[]:
    return x

function id(bool[] x) -> bool[]:
    return x

public export method test():
    assert id(1) == 1
    assert id(id(2)) == 2
    assert id(id(id(3))) == 3
    assert id(id(id(id(4)))) == 4
    assert id(id(id(id(id(5))))) == 5
    assert id(id(id(id(id(id(6)))))) == 6
    assert id(id(id(id(id(id(id(7))))))) == 7
    assert id(id(id(id(id(id(id(id(8)))))))) == 8
    assert id(id(id(id(id(id(id(id(id(9))))))))) == 9
    assert id(id(id(id(id(id(id(id(id(id(10)))))))))) == 10
    //
    assert id(id(id(id(id(id(id(id(id(id(true)))))))))) == true
    assert id(id(id(id(id(id(id(id(id(id([1,2,3,4,5,6])))))))))) == [1,2,3,4,5,6]

    
