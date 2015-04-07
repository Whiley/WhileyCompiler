import whiley.lang.*

type BTree is (null | {
    int item,   // data item
    BTree left, // left subtree
    BTree right // right righttree
} tree) where
    // item in left subtree must be below this item
    (tree != null && tree.left != null ==> tree.left.item < tree.item) &&
    // item in right subtree must be above this item
    (tree != null && tree.right != null ==> tree.right.item > tree.item)

public function BTree() -> BTree:
    return null

public function add(BTree tree, int item) -> BTree:
    if tree == null:
        tree = {item: item, left: null, right: null}
    else:
        if item < tree.item:
            tree.left = add(tree.left, item)
        else:
            tree.right = add(tree.right, item)
    return tree

function contains(BTree tree, int item) -> bool:
    if tree == null:
        return false
    else:
        if tree.item == item:
            return true
        else:
            if item < tree.item:
                return contains(tree.left, item)
            else:
                return contains(tree.right, item)

constant items is [5, 4, 6, 3, 7, 2, 8, 1, 9]

public method main(System.Console console) -> void:
    tree = BTree()
    for item in items:
        tree = add(tree, item)
    for item in items:
        if contains(tree, item):
            console.out.println_s("TREE CONTAINS: " ++ item)
        else:
            console.out.println_s("TREE DOES NOT CONTAIN: " ++ item)
