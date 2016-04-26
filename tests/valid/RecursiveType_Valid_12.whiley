

type Tree is null | Node

type Node is ({int data, Tree rhs, Tree lhs} _this)
where ((_this.lhs == null) || (_this.lhs.data < _this.data)) && ((_this.rhs == null) || (_this.rhs.data > _this.data))

function Tree(int data, Tree left, Tree right) -> Tree
requires ((left == null) || (left.data < data)) && ((right == null) || (right.data > data)):
    return {data: data, rhs: right, lhs: left}

public export method test() :
    Tree l1 = Tree(1, null, null)
    Tree l2 = Tree(3, null, null)
    Tree l3 = Tree(5, null, null)
    Tree t1 = Tree(2, l1, l2)
    assume t1 == {data:2,lhs:{data:1,lhs:null,rhs:null},rhs:{data:3,lhs:null,rhs:null}}
    Tree t2 = Tree(4, t1, l3)
    assume t2 == {data:4,lhs:{data:2,lhs:{data:1,lhs:null,rhs:null},rhs:{data:3,lhs:null,rhs:null}},rhs:{data:5,lhs:null,rhs:null}}

