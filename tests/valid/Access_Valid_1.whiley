

type listdict is [int] | {int=>[int]}

function index(listdict l, int index) -> any:
    return l[index]

public export method test() -> void:
    [int] l = [1, 2, 3]
    assume index(l,0) == 1
    assume index(l,1) == 2
    assume index(l,2) == 3
    {int=>[int]} m = {1=>"hello", 2=>"cruel", 3=>"world"}
    assume index(m,1) == "hello"    
    assume index(m,2) == "cruel"
    assume index(m,3) == "world"
