import println from whiley.lang.System

define nat as int where $ >= 0

// ============================================
// Adjacency List directed graph structure
// ============================================

define Digraph as [{nat}] where no { v in $, w in v | w >= |$| }

Digraph addEdge(Digraph g, nat from, nat to):
    // first, ensure enough capacity
    mx = Math.max(from,to)
    while |g| <= mx:
        g = g + [{}]
    //
    assume from < |g|
    // second, add the actual edge
    g[from] = g[from] + {to}        
    return g

// ============================================
// Test Harness
// ============================================

void ::main(System.Console sys):
    g = []
    g = addEdge(g,1,2)
    g = addEdge(g,2,3)
    g = addEdge(g,3,1)    
    sys.out.println(g)    
