import println from whiley.lang.System

type FileReader is &{int position}

type Reader is {
    FileReader thus, 
    method read(FileReader, int) => int
}

method read(FileReader this, int amount) => int:
    r = amount + this->position
    return r

method openReader() => Reader:
    proc = new {position: 123}
    return {thus: proc, read: &read}

method main(System.Console sys) => void:
    Reader reader = openReader()
    FileReader target = reader.thus
    int data = reader.read(target)
    sys.out.println(Any.toString(data))
