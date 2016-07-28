type MyMeth is method(int) -> int

method read(int x) -> int:
    return x + 123

method test(MyMeth m) -> int:
    return m(false)

method main() -> int:
    return test(&read)
    
