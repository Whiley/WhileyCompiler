import whiley.lang.*

type TYPE is [CONSTANT]

constant CONSTANT is {V1 is TYPE, V2 is TYPE}

constant V1 is [false]

constant V2 is []

method main(System.Console sys) -> void:
    sys.out.println(CONSTANT)
