import * from whiley.lang.System

define IHDR_TYPE as 0x52444849
define IEND_TYPE as 0x444e4549
define IDAT_TYPE as 0x54414449
define PLTE_TYPE as 0x45544c50
define PHYS_TYPE as 0x73594870
public define TIME_TYPE as 0x454d4974

public int f(int type):
    switch type:
        case IHDR_TYPE:
            return 1
        case IEND_TYPE:
            return 2
        case PLTE_TYPE:
            return 3
        case PHYS_TYPE:
            return 4
        case TIME_TYPE:
            return 5
        default:
            return 6    

// hacky test function
public void ::main(System.Console sys):
    sys.out.println("GOT: " + f(IHDR_TYPE))
    sys.out.println("GOT: " + f(IEND_TYPE))
    sys.out.println("GOT: " + f(PLTE_TYPE))
    sys.out.println("GOT: " + f(PHYS_TYPE))
    sys.out.println("GOT: " + f(TIME_TYPE))

