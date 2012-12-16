import println from whiley.lang.System

define InputStream as {
    [byte] ::read(int)
}

define BufferState as ref {
    int pos,
    [byte] bytes
}

[byte] ::read(BufferState state, int amount):
    start = state->pos
    // first, calculate how much can be read
    end = start + Math.min(amount,|state->bytes| - start)
    // second, update bytes pointer
    state->pos = end
    return state->bytes[start .. end]
    
bool ::eof(BufferState state):
    return state->pos >= |state->bytes|

public InputStream BufferInputStream([byte] buffer):
    this = new { pos: 0, bytes: buffer }
    return {
        read: &(int x -> read(this,x))
    }  

void ::main(System.Console sys):
    strings = ["hello","cruel cruel","world"]
    for s in strings:
        // convert string into byte buffer
        bis = BufferInputStream(String.toUTF8(s))
        bytes = bis.read(7)
        sys.out.println("READ: " + String.fromASCII(bytes))
    // done

