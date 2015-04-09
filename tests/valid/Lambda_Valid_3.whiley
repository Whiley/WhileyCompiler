import whiley.lang.*

type string is [int]
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
    InputStream bis = BufferInputStream(ASCII.toBytes("hello"))
    [byte] bytes = bis.read(7)
    assume bytes == [01101000b, 01100101b, 01101100b, 01101100b, 01101111b]
    //
    bis = BufferInputStream(ASCII.toBytes("cruel world"))
    bytes = bis.read(7)    
    assume bytes == [01100011b, 01110010b, 01110101b, 01100101b, 01101100b, 00100000b, 01110111b]
