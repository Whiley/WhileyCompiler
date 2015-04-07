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

method main(System.Console sys) -> void:
    [string] strings = ["hello", "cruel cruel", "world"]
    for s in strings:
        InputStream bis = BufferInputStream(ASCII.toBytes(s))
        while !bis.eof():
            [byte] bytes = bis.read(3)
            sys.out.println_s("READ: " ++ ASCII.fromBytes(bytes))
