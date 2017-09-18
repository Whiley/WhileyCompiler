type FileReader is {int position, byte[] data}

type Reader is {
    method(FileReader, int)->int read
}

function f(Reader r, FileReader state) -> int:
    return r.read(state,0)
