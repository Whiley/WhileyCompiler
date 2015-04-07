import whiley.lang.*

type Tree is null | Node

type Node is {int data, Tree rhs, Tree lhs} where ((lhs == null) || (lhs.data < data)) && ((rhs == null) || (rhs.data > data))

function Tree(int data, Tree left, Tree right) -> Tree
requires ((left == null) || (left.data < data)) && ((right == null) || (right.data > data)):
    return {data: data, rhs: right, lhs: left}

method main(System.Console sys) -> void:
    Tree l1 = Tree(1, null, null)
    Tree l2 = Tree(3, null, null)
    Tree l3 = Tree(5, null, null)
    Tree t1 = Tree(2, l1, l2)
    sys.out.println(t1)
    Tree t2 = Tree(4, t1, l3)
    sys.out.println(t2)
