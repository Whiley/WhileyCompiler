import whiley.lang.*
import whiley.lang.System
import whiley.lang.System

method main(System.Console sys) => void:
    i = 0
    while i < 5:
        if i == 3:
            break
        i = i + 1
    sys.out.println(i)
