import * from whiley.lang.*

define nat as int where $ >= 0
void ::main(System sys,[string] args):
    xs = [1,2,3]
    r = |args|-1
    for x in xs where r >= 0:
        r = r + x    
    debug str(r)
