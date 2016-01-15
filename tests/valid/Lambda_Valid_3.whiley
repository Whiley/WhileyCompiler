type t_Reader is method(int) -> byte[]
type InputStream is { t_Reader read }
type BufferState is &{byte[] bytes, int pos}

// Define the 8bit ASCII character
public type char is (int x) where 0 <= x && x <= 255

// Define ASCII String
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
method read(BufferState state, int amount) -> byte[]
requires amount >= 0:
    //
    byte[] r = [0b; amount]
    int i = 0
    //
    while i < amount && state->pos < |state->bytes|:
        r[i] = state->bytes[state->pos]
        state->pos = state->pos + 1
        i = i + 1
    //
    return r

// Construct buffer from list of bytes
public method BufferInputStream(byte[] buffer) -> InputStream:
    BufferState this = new {bytes: buffer, pos: 0}
    return {read: &(int x -> read(this, x))}

public export method test() :
    InputStream bis = BufferInputStream(toBytes("hello"))
    byte[] bytes = bis.read(7)
    assume bytes == [01101000b, 01100101b, 01101100b, 01101100b, 01101111b, 0b, 0b]
    //
    bis = BufferInputStream(toBytes("cruel world"))
    bytes = bis.read(7)    
    assume bytes == [01100011b, 01110010b, 01110101b, 01100101b, 01101100b, 00100000b, 01110111b]
