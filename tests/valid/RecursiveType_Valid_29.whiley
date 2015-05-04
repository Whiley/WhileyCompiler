

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

public export method test():
    [[int]] words = ["Hat","Cat","Mat","Heat","Hot"]
    // First, initialise trie to include words
    Trie t = Trie()
    t = add(t,"hat")
    t = add(t,"Cat")
    t = add(t,"Mat")
    t = add(t,"Heat")
    t = add(t,"Hat")
    // Second check tries
    assume !contains(t,"Pat")
    assume contains(t,"Hat")
    assume contains(t,"Cat")

