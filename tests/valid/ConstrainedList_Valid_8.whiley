import whiley.lang.*

type nat is (int x) where x >= 0

function update([nat] list, nat index, nat value) -> [nat]
requires index < |list|:
    list[index] = value
    return list

method main(System.Console console) -> void:
    [nat] xs = [1, 2, 3, 4]
    xs = update(xs, 0, 2)
    xs = update(xs, 1, 3)
    xs = update(xs, 2, 4)
    xs = update(xs, 3, 5)
    console.out.println(xs)
