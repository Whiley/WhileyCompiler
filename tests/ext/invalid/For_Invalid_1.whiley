

define nat as int where $ >= 0
void ::main(System.Console sys):
    xs = [1,2,3]
    r = |args|-1
    for x in xs where r >= 0:
        r = r + x    
    debug Any.toString(r)
