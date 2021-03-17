function f<T>(T[] arr) -> T[]:
    //
    return []

function g<T>(T[] arr, T def) -> T[]:
    //
    return [def; |arr|]

public export method test():
    //
    assume f([1,2,3]) == []
    assume f([true,false]) == []
    //
    assume g([],0) == []
    assume g([1,2,3],0) == [0,0,0]
    assume g([true,false],false) == [false,false]
    