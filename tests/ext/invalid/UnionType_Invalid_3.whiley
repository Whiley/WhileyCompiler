import * from whiley.lang.*

define nat as int where $ >= 0
define natlist as [nat]
define nlt as nat | natlist

nlt g(int y):
    return y

void ::main(System.Console sys):
    g(-1)
