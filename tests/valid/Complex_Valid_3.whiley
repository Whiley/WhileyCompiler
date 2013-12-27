// Basic implementation of an (unbalanced) binary tree
// David J. Pearce, 2012

import println from whiley.lang.System

public define BTree as null | { 
    int item, 
    BTree left,
    BTree right
} 

// where ($.left == null || $.left.data < $.data) && ($.right == null || $.item < $.right.item)

public BTree BTree():
    return null

// Add a given item into the tree
public BTree add(BTree tree, int item):
    if tree == null:
        tree = { item: item, left: null, right: null }
    else if item < tree.item:
        tree.left = add(tree.left,item)
    else:
        tree.right = add(tree.right,item)
    return tree
        
// Check whether a given tree contains a given item.
bool contains(BTree tree, int item):
    if tree == null:
        // tree is empty, so not contained
        return false
    else:
        if tree.item == item:
            return true
        else if item < tree.item:
            return contains(tree.left,item)
        else:
            return contains(tree.right,item)

define items as [5,4,6,3,7,2,8,1,9]

public void ::main(System.Console console):
    tree = BTree()
    // add items into tree
    for item in items:
        tree = add(tree,item)  
    // check if items in tree
    for item in items:
        if contains(tree,item):
            console.out.println("TREE CONTAINS: " + item)
        else:
            console.out.println("TREE DOES NOT CONTAIN: " + item)
    // done
    