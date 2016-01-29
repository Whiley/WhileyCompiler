

type BTree is null | {int item, BTree left, BTree right}

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

public export method test() :
    BTree tree = BTree()
    tree = add(tree, 1)
    tree = add(tree, 2)
    tree = add(tree, 3)
    tree = add(tree, 4)
    tree = add(tree, 5)
    tree = add(tree, 6)
    //
    assume contains(tree,5) == true
    assume contains(tree,4) == true
    assume contains(tree,6) == true
    assume contains(tree,3) == true
    assume contains(tree,7) == false
    assume contains(tree,2) == true
    assume contains(tree,8) == false
    assume contains(tree,1) == true
    assume contains(tree,9) == false
