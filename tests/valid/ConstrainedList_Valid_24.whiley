type nat is (int x) where x >= 0

type Digraph is ([{nat}] edges) where no { v in edges, w in v | w >= |edges| }

function max(nat a, nat b) -> nat:
    if a >= b:
        return a
    else:
        return b

function addEdge(Digraph g, nat from, nat to) -> Digraph:
    int mx = max(from, to)
    while |g| <= mx:
        g = g ++ [{}]
    assert from < |g| && |g| > to
    g[from] = g[from] + {to}
    return g

public export method test() -> void:
    Digraph g = []
    g = addEdge(g, 1, 2)
    g = addEdge(g, 2, 3)
    g = addEdge(g, 3, 1)
    assume g == [{}, {2}, {3}, {1}]

