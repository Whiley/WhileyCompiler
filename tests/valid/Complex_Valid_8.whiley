// Represents a transition from one
// state to another for a given ASCII.character.
type Transition is {
    int from,
    int to,
    ASCII.char character
} where
    from >= 0 && to >= 0 &&
    from < to

// A Finite State Machine representation of a Trie
type Trie is {
    [Transition] transitions
}

// Define the Empty Trie
constant EmptyTrie is { transitions: [] }

// Add a complete ASCII.string into a Trie starting from the root node.
function add(Trie trie, ASCII.string str) -> Trie:
    return add(trie,0,str)

// Add a ASCII.string into a Trie from a given state, producing an
// updated Trie.
function add(Trie trie, int state, ASCII.string str) -> Trie
requires state >= 0:
    //
    if |str| == 0:
        return trie
    else:
        //
        // Check whether transition exists for first
        // ASCII.character of str already.
        ASCII.char c = str[0]
        int i = 0
        //
        while i < |trie.transitions| where i >= 0:
            Transition t = trie.transitions[i]
            if t.from == state && t.character == c:
                // Yes, existing transition for ASCII.character
                return add(trie,t.to,str[1..])
            i = i + 1
        //
        // No existing transition, so make a new one.
        int target = |trie.transitions| + 1
        Transition t = { from: state, to: target, character: c }
        trie.transitions = trie.transitions ++ [t]
        return add(trie,target,str[1..])

// Check whether a given ASCII.string is contained in the trie,
// starting from the root state.
function contains(Trie trie, ASCII.string str) -> bool:
    return contains(trie,0,str)

// Check whether a given ASCII.string is contained in the trie,
// starting from a given state.
function contains(Trie trie, int state, ASCII.string str) -> bool
requires state >= 0:
    //
    if |str| == 0:
        return true
    else:
        // Check whether transition exists for first
        // ASCII.character of str.
        ASCII.char c = str[0]
        int i = 0
        //
        while i < |trie.transitions| where i >= 0:
            Transition t = trie.transitions[i]
            if t.from == state && t.character == c:
                // Yes, existing transition for ASCII.character
                return contains(trie,t.to,str[1..])
            i = i + 1
        //
        return false

method main(System.Console console):
    Trie t = EmptyTrie
    // First, initialise trie
    for s in ["hello","world","help"]:
        console.out.println_s("ADDING: " ++ s)
        t = add(t,s)
    // Second, check containment
    for s in ["hello","blah","hel","dave"]:
        bool r = contains(t,s)
        console.out.println_s("CONTAINS: " ++ s ++ " = " ++ Any.toString(r))
