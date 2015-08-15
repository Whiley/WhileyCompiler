type FileReader is {int position, [byte] data}

type Reader is {
    method(FileReader, int)->int read
}

function f(Reader r) -> int:
    return 1
