public type Element<T> is { Node<T>[] children }
public type Node<T> is  Element<T> | int[]

public function h1<T>(Node<T> child) -> Node<T>:
    return { children: [child] }

public function div<T>(Node<T>[] children) -> Node<T>:
    return { children: children }

public function read<T>(Node<T> n) -> (int[] string):
    if n is int[]:
        return n
    else if |n.children| > 0:
        return read(n.children[0])
    else:
        return ""

public export method test():
    // Construct root node
    Node<int> n = div([h1("test")])
    //
    assume read(n) == "test"

