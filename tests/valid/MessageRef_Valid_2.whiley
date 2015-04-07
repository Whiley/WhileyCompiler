import whiley.lang.*

type FileReader is &{int position}

type Reader is {
    FileReader thus,
    method read(FileReader, int) -> int
}

method read(FileReader this, int amount) -> int:
    int r = amount + this->position
    return r

method openReader() -> Reader:
    FileReader proc = new {position: 123}
    return {thus: proc, read: &read}

method main(System.Console sys) -> void:
    Reader reader = openReader()
    FileReader target = reader.thus
    int data = reader.read(target, 1)
    sys.out.println(data)
