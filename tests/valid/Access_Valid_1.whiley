import whiley.lang.*

type listdict is [int] | {int=>[int]}

function index(listdict l, int index) -> any:
    return l[index]

method main(System.Console sys) -> void:
    [int] l = [1, 2, 3]
    assert index(l,0) == 1
    assert index(l,1) == 2
    assert index(l,2) == 3
    {int=>[int]} m = {1=>"hello", 2=>"cruel", 3=>"world"}
    assert index(m,1) == "hello"    
    assert index(m,2) == "cruel"
    assert index(m,3) == "world"
