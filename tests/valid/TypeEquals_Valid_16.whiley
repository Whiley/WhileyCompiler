type src is int | src[]

function f(src e) -> bool:
    if e is any[]:
        return true
    else:
        return false

public export method test() :
    assume f([1]) == true
    assume f([[1]]) == true
    assume f([[[1]]]) == true
    assume f(1) == false
