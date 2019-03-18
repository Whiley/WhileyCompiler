function f<T>(T[] arr) -> T[]:
    //
    return []

function g<T>(T[] arr, T def) -> T[]:
    //
    return [def; |arr|]

public export method test():
    //
    assert f([1,2,3]) == []
    assert f([true,false]) == []
    //
    assert g([],0) == []
    assert g([1,2,3],0) == [0,0,0]
    assert g([true,false],false) == [false,false]
    