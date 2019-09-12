
type bintref is ((&bool)|(&int) n)

method main(bintref ptr):
    //
    if ptr is &int:
        (*ptr) = 123
    //
    assume ptr is &bool || *ptr == 123

public export method test() :
    main(new 0)
    
