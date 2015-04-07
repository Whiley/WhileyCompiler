import whiley.lang.*

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

method main(System.Console sys) -> void:
    sys.out.println_s("NUM: " ++ Any.toString(f(Red)))
    sys.out.println_s("NUM: " ++ Any.toString(f(Green)))
    sys.out.println_s("NUM: " ++ Any.toString(f(Blue)))
