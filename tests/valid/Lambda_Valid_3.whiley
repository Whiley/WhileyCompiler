import whiley.lang.System

type t_Reader is method(int) -> [byte]

type InputStream is { t_Reader read }

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
    return {read: &(int x -> read(this, x))}

method main(System.Console sys) -> void:
    [ASCII.string] strings = ["hello", "cruel cruel", "world"]
    for s in strings:
        InputStream bis = BufferInputStream(ASCII.toBytes(s))
        [byte] bytes = bis.read(7)
        sys.out.print("READ: ")
        sys.out.println(bytes)
