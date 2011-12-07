import whiley.lang.*
import * from whiley.lang.Errors
import * from whiley.lang.System

void ::main(System sys, [string] args):
    i = 0
    while i< 5:
        if(i == 3):
            break
        i = i+1
    sys.out.println(i)
      
