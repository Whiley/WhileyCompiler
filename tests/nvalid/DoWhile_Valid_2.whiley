import whiley.lang.*
import println from whiley.lang.System
import println from whiley.lang.System

method main(System.Console sys) => void:
    i = 0
    do:
        if i == 2:
            break
        i = i + 1
    while i < 5
    sys.out.println(i)
