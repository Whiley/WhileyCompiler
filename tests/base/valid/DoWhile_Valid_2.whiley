import whiley.lang.*
import * from whiley.lang.Errors
import * from whiley.lang.System

void ::main(System sys, [string] args):
    i = 0
    do:
        if i == 2:
            break
        i = i+1
    while i < 5
    sys.out.println(i)
      
