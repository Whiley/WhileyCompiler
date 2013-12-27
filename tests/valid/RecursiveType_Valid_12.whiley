import println from whiley.lang.System

define Tree as null | Node

define Node as { 
    int data, 
    Tree lhs, 
    Tree rhs 
} where (lhs == null || lhs.data < data) && 
        (rhs == null || rhs.data > data)

Tree Tree(int data, Tree left, Tree right) 
        requires (left == null || left.data < data) &&
                (right == null || right.data > data):
    return {data: data, lhs: left, rhs: right}

void ::main(System.Console sys):
    l1 = Tree(1,null,null)
    l2 = Tree(3,null,null)
    l3 = Tree(5,null,null)
    t1 = Tree(2,l1,l2)
    sys.out.println(t1)
    t2 = Tree(4,t1,l3)
    sys.out.println(t2)

