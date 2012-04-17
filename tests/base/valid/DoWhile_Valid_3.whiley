import whiley.lang.*
import println from whiley.lang.System
import println from whiley.lang.System

void ::main(System.Console sys):
    i = 0
    do:
        if i == 2:
            continue
        i = i+1
    while i < 5
    sys.out.println(i)
      
