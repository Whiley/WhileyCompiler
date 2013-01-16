import println from whiley.lang.*

define nat as int where $ >= 0

[nat] abs([int] items) requires |items| > 0:
    return abs(items,0)

[nat] abs([int] items, nat index) requires index < |items| && all { i in 0..index | items[i] >= 0 }:
    if index == |items|:
        return items
    else:
        items[index] = Math.abs(items[index])
        return abs(items,index+1)

    
void ::main(System.Console sys):
    xs = [1,-3,-5,7,-9,11]
    xs = abs(xs)
    sys.out.println(Any.toString(xs))
