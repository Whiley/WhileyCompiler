import * from whiley.lang.*

method main(System.Console sys) -> void:
    real end = 1.2344
    [int] list = [1, 2, 3]
    [int] sublist = list[-1..end]
    sys.out.println(Any.toString(list))
    sys.out.println(Any.toString(sublist))
