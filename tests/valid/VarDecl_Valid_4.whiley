import whiley.lang.*

function constantPool() -> int:
    return 12478623847120981

method main(System.Console sys) -> void:
    int pool = constantPool()
    sys.out.println_s("GOT: " ++ Any.toString(pool))
