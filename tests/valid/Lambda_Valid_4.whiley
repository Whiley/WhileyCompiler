type InputStream is {
    method read(int) -> byte[],
    method eof() -> bool
}

type BufferState is &{byte[] bytes, int pos}

// Define the 8bit ASCII character
public type char is (int x) where 0 <= x && x <= 255

// Define 8bit ASCII String
type string is char[]

// Convert an ASCII character into a byte.
public function toByte(char v) -> byte:
    //
    byte mask = 00000001b
    byte r = 0b
    int i = 0
    while i < 8:
        if (v % 2) == 1:
            r = r | mask
        v = v / 2
        mask = mask << 1
        i = i + 1
    return r

// Convert an ASCII string into a list of bytes
public function toBytes(string s) -> byte[]:
    byte[] r = [0b; |s|]
    int i = 0
    while i < |s| where i >= 0:
        r[i] = toByte(s[i])
        i = i + 1
    return r

// Compute minimum of two integers
function min(int a, int b) -> int:
    if a <= b:
        return a
    else:
        return b

// Read specified number of bytes from buffer
method read(BufferState state, int amount) -> byte[]:
    byte[] r = [0b; amount]
    int i = 0
    //
    while i < amount && state->pos < |state->bytes|:
        r[i] = state->bytes[state->pos]
        state->pos = state->pos + 1
        i = i + 1
    //
    return r

// Check whether buffer is empty or not
method eof(BufferState state) -> bool:
    return state->pos >= |state->bytes|

// Construct buffer from list of bytes
public method BufferInputStream(byte[] buffer) -> InputStream:
    BufferState this = new {bytes: buffer, pos: 0}
    return {
        read: &(int x -> read(this, x)),
        eof: &( -> eof(this))
    }

method read(string s) -> byte[]:
    byte[] bytes = [0b;0]
    InputStream bis = BufferInputStream(toBytes(s))
    //
    while !bis.eof():
        bytes = bis.read(3)
    //
    return bytes
        
public export method test() :
    assume read("hello") == [01101100b, 01101111b, 0b]
    
