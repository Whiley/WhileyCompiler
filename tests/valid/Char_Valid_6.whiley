import whiley.lang.System

constant digits is ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9']

constant alphabet is ['a', 'b', 'c', 'd', 'e', 'f']

function iof(int i) => string:
    return "" ++ alphabet[i % 6] ++ digits[i % 10]

method main(System.Console sys) => void:
    for i in 0 .. 100:
        sys.out.println(Any.toString(iof(i)))
