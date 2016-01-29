

constant IHDR_TYPE is 1380206665

constant IEND_TYPE is 1145980233

constant IDAT_TYPE is 1413563465

constant PLTE_TYPE is 1163152464

constant PHYS_TYPE is 1935231088

constant TIME_TYPE is 1162692980

public function f(int type) -> int:
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

public export method test() :
    assume f(IHDR_TYPE) == 1
    assume f(IEND_TYPE) == 2
    assume f(PLTE_TYPE) == 3
    assume f(PHYS_TYPE) == 4
    assume f(TIME_TYPE) == 5
