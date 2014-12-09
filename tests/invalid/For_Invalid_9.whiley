function List([int] items, int item) -> bool:
    for i in items:
        if item == i:
            return true
        return false // error!
    return false