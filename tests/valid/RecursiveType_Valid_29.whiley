type Trie is {
   {char=>Trie} children
}

function Trie() => Trie:
    return {
        children: {=>} // no children
    }

function add(Trie t, string s) => Trie:
    //
    if(|s| == 0):
        return t
    else:
        char c = s[0]
        //
        for ch,subtrie in t.children:
            if ch == c && subtrie != null:
                // Node for char c already exists
                t.children[c] = add(subtrie,s[1..])
                return t
        // Node for char c does not exist
        t.children[c] = add(Trie(),s[1..])
        return t

function contains(Trie t, string s) => bool:
    if |s| == 0:
        return true
    else:
        char c = s[0]
        //
        for ch,subtrie in t.children:
            if ch == c && subtrie != null:
                return contains(subtrie,s[1..])
        //
        return false

method main(System.Console console):
    [string] words = ["Hat","Cat","Mat","Heat","Hot"]
    // First, initialise trie to include words
    Trie t = Trie()
    for w in words:
        t = add(t,w)
    // Second print out trie
    for w in ["Pat","Hat","Cat"]:
        bool f = contains(t,w)
        console.out.println("CONTAINS(" ++ w ++ ") = " ++ f)

