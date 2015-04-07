import whiley.lang.*

method main(System.Console console):
    int x = 8
    int y = 1
    int z = x * x - y
    console.out.println_s("8 * 8 - 1 => " ++ Any.toString(z))
    z = (x * x) - y
    console.out.println_s("(8 * 8) - 1 => " ++ Any.toString(z))
    z = x * (x - y)
    console.out.println_s("8 * (8 - 1) => " ++ Any.toString(z))
