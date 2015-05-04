

public export method test() -> void:
    {int} aset = {1, 21, 3, 4, 1}
    {int} bset = {2, 2, 3, 4, 9}
    {int|bool|real} cset = {-1.0, 25, true, 7, 13.4, false}
    assume (aset + bset) == {1,2,3,4,9,21}
    assume (aset & bset) == {3,4}
    assume (cset + aset) == {1, 21, 3, 4,-1.0, 25, true, 7, 13.4, false}
    assume (cset & aset) == {}
