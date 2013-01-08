import println from whiley.lang.System

int indexOf([int] list, int index) 
    requires all { l in list | l >= 0 } && 
            index >= 0 && index < |list|:
    return list[index]

void ::main(System.Console sys):
    items = [5,4,6,3,7,2,8,1]
    for i in 0..|items|:
        sys.out.println(indexOf(items,i))    
