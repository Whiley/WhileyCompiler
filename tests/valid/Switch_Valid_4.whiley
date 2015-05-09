

constant Red is 1

constant Blue is 2

constant Green is 3

type RGB is (int x) where x in {Red, Blue, Green}

function f(RGB c) -> int:
    switch c:
        case Red:
            return 123
        case Switch_Valid_4.Blue:
            return 234
        default:
            return 456

public export method test() -> void:
    assume f(Red) == 123
    assume f(Green) == 456
    assume f(Blue) == 234
