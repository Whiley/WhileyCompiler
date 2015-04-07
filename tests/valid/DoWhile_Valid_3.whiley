import whiley.lang.*
import whiley.lang.*
import whiley.lang.*

method main(System.Console sys) -> void:
    int i = 0
    do:
        if i == 2:
            break
        i = i + 1
    while i < 5
    sys.out.println(i)
