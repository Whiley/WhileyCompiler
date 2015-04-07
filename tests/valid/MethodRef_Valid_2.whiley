import whiley.lang.*

type Reader is {
  method read(int) -> int
}

method f(int amount) -> int:
    return 1

method m(Reader r, int x) -> int:
    return r.read(x)

method main(System.Console sys) -> void:
    Reader reader = {read: &f}
    int data = m(reader, 1)
    sys.out.println(data)
