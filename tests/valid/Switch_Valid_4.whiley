import whiley.lang.System

constant Red is 1

constant Blue is 2

constant Green is 3

constant RGB is {Red, Blue, Green}

function f(RGB c) => int:
    switch c:
        case Red:
            return 123
        case Switch_Valid_4.Blue:
            return 234
        default:
            return 456

method main(System.Console sys) => void:
    sys.out.println("NUM: " ++ f(Red))
    sys.out.println("NUM: " ++ f(Green))
    sys.out.println("NUM: " ++ f(Blue))
