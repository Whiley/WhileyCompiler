method main(System.Console console):
    int x = 8
    int y = 1
    int z = x * x - y
    console.out.println("8 * 8 - 1 => " ++ z)
    z = (x * x) - y
    console.out.println("(8 * 8) - 1 => " ++ z)
    z = x * (x - y)
    console.out.println("8 * (8 - 1) => " ++ z)
