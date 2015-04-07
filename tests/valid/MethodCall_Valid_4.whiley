import whiley.lang.*

type Sum is &{int result, [int] items}

method start(Sum this) -> void:
    int sum = 0
    for i in this->items:
        sum = sum + i
    this->result = sum

method get(Sum this) -> int:
    return this->result

method create([int] items) -> Sum:
    return new {result: 0, items: items}

method seqSum([int] items) -> int:
    int r = 0
    for i in items:
        r = r + i
    return r

method parSum([int] items) -> int:
    Sum sum = create(items)
    start(sum)
    return get(sum)

type pst is method ([int])->int

method sum(pst m, [int] data) -> int:
    return m(data)

method main(System.Console sys) -> void:
    [int] data = [1, 3, 5, 7, 3, 198, 1, 4, 6]
    int s1 = sum(&parSum, data)
    sys.out.println_s("SUM: " ++ Any.toString(s1))
    int s2 = sum(&seqSum, data)
    sys.out.println_s("SUM: " ++ Any.toString(s2))
