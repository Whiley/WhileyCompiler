import whiley.lang.*

type string is [int]

type InputStream is {
    method read(int) -> [byte],
    method eof() -> bool
}

type BufferState is &{[byte] bytes, int pos}

method read(BufferState state, int amount) -> [byte]:
    int start = state->pos
    int end = start + Math.min(amount, |state->bytes| - start)
    state->pos = end
    return state->bytes[start..end]

method eof(BufferState state) -> bool:
    return state->pos >= |state->bytes|

public method BufferInputStream([byte] buffer) -> InputStream:
    BufferState this = new {bytes: buffer, pos: 0}
    return {read: &(int x -> read(this, x)), eof: &( -> eof(this))}

method read(string s) -> [byte]:
    [byte] bytes = []
    InputStream bis = BufferInputStream(ASCII.toBytes(s))
    //
    while !bis.eof():
        bytes = bis.read(3)
    //
    return bytes
        
method main(System.Console sys) -> void:
    assume read("hello") == [01101100b, 01101111b]
    