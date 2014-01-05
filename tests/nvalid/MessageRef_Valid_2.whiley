import println from whiley.lang.System

type FileReader is &{int position}

type Reader is {FileReader thus, int ::(FileReader, int) read}

method read(FileReader this, int amount) => int:
    r = amount + this->position
    return r

method openReader() => Reader:
    proc = new {position: 123}
    return {thus: proc, read: &read}

method main(System.Console sys) => void:
    reader = openReader()
    target = reader.thus
    method = reader.read
    data = target.method(5)
    sys.out.println(Any.toString(data))
