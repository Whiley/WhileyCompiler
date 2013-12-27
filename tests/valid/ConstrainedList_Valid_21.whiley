import println from whiley.lang.System

define nat as int where $ >= 0

[nat] update([nat] list, nat index, nat value) requires index < |list|:
    list[index] = value
    return list

void ::main(System.Console console):
    xs = [1,2,3,4]
    xs = update(xs,0,2)
    xs = update(xs,1,3)
    xs = update(xs,2,4)
    xs = update(xs,3,5)
    console.out.println(xs)