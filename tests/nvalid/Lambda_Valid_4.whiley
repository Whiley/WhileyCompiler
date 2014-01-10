import println from whiley.lang.System

type InputStream is {            
    method read(int) => [byte],  
    method eof() => bool         
}

type BufferState is &{[byte] bytes, int pos}

method read(BufferState state, int amount) => [byte]:
    start = state->pos
    end = start + Math.min(amount, |state->bytes| - start)
    state->pos = end
    return state->bytes[start..end]

method eof(BufferState state) => bool:
    return state->pos >= |state->bytes|

public method BufferInputStream([byte] buffer) => InputStream:
    this = new {bytes: buffer, pos: 0}
    return {read: &(int x -> read(this, x)), eof: &( -> eof(this))}

method main(System.Console sys) => void:
    strings = ["hello", "cruel cruel", "world"]
    for s in strings:
        bis = BufferInputStream(String.toUTF8(s))
        while !bis.eof():
            bytes = bis.read(3)
            sys.out.println("READ: " + String.fromASCII(bytes))
