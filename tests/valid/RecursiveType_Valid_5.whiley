

type Link is null | {int[] items, Link next}

method create(int n) -> Link:
    Link start = null
    int i = 0
    while i < n:
        start = {items: [0;0], next: start}
        i = i + 1
    return start
