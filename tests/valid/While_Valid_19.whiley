import println from whiley.lang.System

constant SIZE is 5

method main(System.Console sys) => void:
    components = []
    while |components| < SIZE:
        components = components ++ [{}]
    sys.out.println(components)
