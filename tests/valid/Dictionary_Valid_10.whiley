import whiley.lang.*

public method main(System.Console sys) -> void:
    {int=>int} l = {1=>2, 2=>3}
    sys.out.println_s("Dictionary: " ++ Any.toString(l))
    sys.out.println_s("Length: " ++ Any.toString(|l|))
    l[3] = 123
    sys.out.println_s("Dictionary: " ++ Any.toString(l))
    sys.out.println_s("Length: " ++ Any.toString(|l|))
