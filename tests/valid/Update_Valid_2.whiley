import whiley.lang.*

type list is [int]

function update(list l, int index, int value) -> list:
    l[index] = value
    return l

method main(System.Console sys) -> void:
    l = ['1', '2', '3']
    assume update(l, 1, 0) == ['1',0,'3']
    assume update(l, 2, 0) == ['1','2',0]
    l = "Hello World"
    assume update(l, 1, 0) == ['H',0,'l','l','o',' ','W','o','r','l','d']
    assume update(l, 2, 0) == ['H','e',0,'l','o',' ','W','o','r','l','d']
