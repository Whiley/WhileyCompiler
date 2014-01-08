import * from whiley.lang.*

type intlist is {int}

method main(System.Console sys) => void:
    il = {1, 2, 3}
    sys.out.println(|il|)
    sys.out.println(il[0])
