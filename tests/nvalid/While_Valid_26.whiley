import println from whiley.lang.System

type nat is int where $ >= 0

function search([int] items, int item) => null | nat
requires all { i in 0 .. (|items| - 1) | items[i] < items[i + 1] }
ensures ($ is null) || (items[$] == item)
ensures ($ is int) || no { i in items | i == item }:
    i = 0
    while i < |items| where i >= 0, where all { j in 0 .. i | items[j] != item }:
        if items[i] == item:
            return i
        i = i + 1
    return null

method main(System.Console console) => void:
    list = [3, 5, 6, 9]
    console.out.println(search(list, 0))
    console.out.println(search(list, 1))
    console.out.println(search(list, 2))
    console.out.println(search(list, 3))
    console.out.println(search(list, 4))
    console.out.println(search(list, 5))
    console.out.println(search(list, 6))
    console.out.println(search(list, 7))
    console.out.println(search(list, 8))
    console.out.println(search(list, 9))
    console.out.println(search(list, 10))
