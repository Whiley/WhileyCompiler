import whiley.lang.*
import whiley.lang.System
import whiley.lang.System

method main(System.Console sys) => void:
    int i = 0
    do:
        if i == 2:
            break
        i = i + 1
    while i < 5
    sys.out.println(i)
