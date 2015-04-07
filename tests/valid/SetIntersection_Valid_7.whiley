import whiley.lang.*

method main(System.Console sys) -> void:
    {int} aset = {1, 21, 3, 4, 1}
    {int} bset = {2, 2, 3, 4, 9}
    {int|bool|real} cset = {-1.0, 25, true, 7, 13.4, false}
    sys.out.println(aset + bset)
    sys.out.println(aset & bset)
    sys.out.println(cset + aset)
    sys.out.println(cset & aset)
