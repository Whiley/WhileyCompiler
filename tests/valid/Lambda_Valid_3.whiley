type string is [int]
type t_Reader is method(int) -> [byte]
type InputStream is { t_Reader read }
type BufferState is &{[byte] bytes, int pos}

// Define the 8bit ASCII character
public type char is (int x) where 0 <= x && x <= 255

// Convert an ASCII character into a byte.
public function toByte(char v) -> byte:
    //
    byte mask = 00000001b
    byte r = 0b
    for i in 0..8:
        if (v % 2) == 1:
            r = r | mask
        v = v / 2
        mask = mask << 1
    return r

// Convert an ASCII string into a list of bytes
public function toBytes(string s) -> [byte]:
    [byte] r = []
    int i = 0
    while i < |s|:
        r = r ++ [toByte(s[i])]
        i = i + 1
    return r

// Compute minimum of two integers
function min(int a, int b) -> int:
    if a <= b:
        return a
    else:
        return b

// Read specified number of bytes from buffer
method read(BufferState state, int amount) -> [byte]:
    int start = state->pos
    int end = start + min(amount, |state->bytes| - start)
    state->pos = end
    return state->bytes[start..end]

// Construct buffer from list of bytes
public method BufferInputStream([byte] buffer) -> InputStream:
    BufferState this = new {bytes: buffer, pos: 0}
    return {read: &(int x -> read(this, x))}

public export method test() -> void:
    InputStream bis = BufferInputStream(toBytes("hello"))
    [byte] bytes = bis.read(7)
    assume bytes == [01101000b, 01100101b, 01101100b, 01101100b, 01101111b]
    //
    bis = BufferInputStream(toBytes("cruel world"))
    bytes = bis.read(7)    
    assume bytes == [01100011b, 01110010b, 01110101b, 01100101b, 01101100b, 00100000b, 01110111b]
