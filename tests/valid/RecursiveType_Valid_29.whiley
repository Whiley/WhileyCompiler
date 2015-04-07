import whiley.lang.*

type Trie is {
   {int=>Trie} children
}

function Trie() -> Trie:
    return {
        children: {=>} // no children
    }

function add(Trie t, [int] s) -> Trie:
    //
    if(|s| == 0):
        return t
    else:
        int c = s[0]
        //
        for ch,subtrie in t.children:
            if ch == c:
                // Node for c already exists
                t.children[c] = add(subtrie,s[1..])
                return t
        // Node for c does not exist
        t.children[c] = add(Trie(),s[1..])
        return t

function contains(Trie t, [int] s) -> bool:
    if |s| == 0:
        return true
    else:
        int c = s[0]
        //
        for ch,subtrie in t.children:
            if ch == c:
                return contains(subtrie,s[1..])
        //
        return false

method main(System.Console console):
    [[int]] words = ["Hat","Cat","Mat","Heat","Hot"]
    // First, initialise trie to include words
    Trie t = Trie()
    for w in words:
        t = add(t,w)
    // Second print out trie
    for w in ["Pat","Hat","Cat"]:
        bool f = contains(t,w)
        console.out.println_s("CONTAINS(" ++ w ++ ") = " ++ Any.toString(f))

