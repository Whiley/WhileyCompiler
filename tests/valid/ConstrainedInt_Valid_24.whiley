import whiley.lang.System

//
// This little example is showing off an almost complete encoding
// of C ASCII.strings as constrained lists of ints in Whiley.  The key 
// requirements are that the list contains only ASCII ASCII.characters, 
// and that it is null terminated.
//
// The only outstanding problem with this encoding is that it embeds 
// the list size (i.e. there is currently no way to get rid of this).
//
type ASCII_char is (int n) where 0 <= n && n <= 255

type C_string is ([ASCII_char] chars) 
// Must have at least one ASCII.character (i.e. null terminator)
where |chars| > 0 && chars[|chars|-1] == 0

// Determine the length of a C ASCII.string.
public function strlen(C_string str) -> (int r)
ensures r >= 0:
    //
    int i = 0
    //
    while str[i] != 0 
        where i >= 0 && i < |str|:
        //
        i = i + 1
    //
    return i

// Print out hello world!
public method main(System.Console console):
    C_string hw = ([int]) ['H','e','l','l','o','W','o','r','l','d',0]
    console.out.println(strlen(hw))
