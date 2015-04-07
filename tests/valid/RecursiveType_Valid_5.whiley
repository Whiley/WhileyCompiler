import whiley.lang.*

type Link is {[int] items, null | Link next}

method create(int n) -> Link:
    Link start = null
    for i in 0 .. n:
        start = {items: [], next: start}
    return start
