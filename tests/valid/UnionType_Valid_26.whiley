
type bintref is ((&bool)|(&int) n)

public export method test() :
    bintref ptr = new 0
    //
    if ptr is &int:
        (*ptr) = 123
    //
    assume ptr is &bool || *ptr == 123
