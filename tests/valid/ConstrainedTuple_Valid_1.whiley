import println from whiley.lang.System

define nat as int where $ >= 0
define tnat as (nat,nat)

nat f(tnat tup):
    x,y = tup
    return x+y

public void ::main(System.Console console):
    x = (3,5)
    console.out.println("GOT: " + f(x))