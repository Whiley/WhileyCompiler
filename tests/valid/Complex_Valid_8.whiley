type char is int
type string is int[]

// Represents a transition from one
// state to another for a given character.
type Transition is {
    int from,
    int to,
    char character
} where
    from >= 0 && to >= 0 &&
    from < to

// A Finite State Machine representation of a Trie
type Trie is {
    Transition[] transitions
}

// Define the Empty Trie
constant DummyTransition is { from: 0, to: 0, char: 0 }
constant EmptyTrie is { transitions: [DummyTransition; 0] }

function append(Transition[] transitions, Transition t) -> Transition[]:
    Transition[] r = [t; |transitions| + 1]
    int i = 0
    while i < |transitions| where i >= 0:
        r[i] = transitions[i]
        i = i + 1
    return r

// Add a complete string into a Trie starting from the root node.
function add(Trie trie, string str) -> Trie:
    return add(trie,0,str,0)

// Add a string into a Trie from a given state, producing an
// updated Trie.
function add(Trie trie, int state, string str, int index) -> Trie
requires state >= 0:
    //
    if |str| == index:
        return trie
    else:
        //
        // Check whether transition exists for first
        // character of str already.
        char c = str[index]
        int i = 0
        //
        while i < |trie.transitions| where i >= 0:
            Transition t = trie.transitions[i]
            if t.from == state && t.character == c:
                // Yes, existing transition for character
                return add(trie,t.to,str,index+1)
            i = i + 1
        //
        // No existing transition, so make a new one.
        int target = |trie.transitions| + 1
        Transition t = { from: state, to: target, character: c }
        trie.transitions = append(trie.transitions,t)
        return add(trie,target,str,index+1)

// Check whether a given string is contained in the trie,
// starting from the root state.
function contains(Trie trie, string str) -> bool:
    return contains(trie,0,str,0)

// Check whether a given string is contained in the trie,
// starting from a given state.
function contains(Trie trie, int state, string str, int index) -> bool
requires state >= 0:
    //
    if |str| == index:
        return true
    else:
        // Check whether transition exists for first
        // character of str.
        char c = str[index]
        int i = 0
        //
        while i < |trie.transitions| where i >= 0:
            Transition t = trie.transitions[i]
            if t.from == state && t.character == c:
                // Yes, existing transition for character
                return contains(trie,t.to,str,index+1)
            i = i + 1
        //
        return false

public export method test():
    Trie t = EmptyTrie
    // First, initialise trie
    t = add(t,"hello")
    t = add(t,"world")
    t = add(t,"help")
    // Second, check containment
    assume contains(t,"hello")
    assume !contains(t,"blah")
    assume contains(t,"hel")
    assume !contains(t,"dave")
