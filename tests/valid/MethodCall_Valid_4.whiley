import println from whiley.lang.System

type Sum is &{int result, [int] items}

method start(Sum this) => void:
    sum = 0
    for i in this->items:
        sum = sum + i
    this->result = sum

method get(Sum this) => int:
    return this->result

method create([int] items) => Sum:
    return new {result: 0, items: items}

function seqSum([int] items) => int:
    r = 0
    for i in items:
        r = r + i
    return r

method parSum([int] items) => int:
    sum = create(items)
    sum.start()
    return sum.get()

method sum(int ::([int]) m, [int] data) => int:
    return m(data)

method main(System.Console sys) => void:
    data = [1, 3, 5, 7, 3, 198, 1, 4, 6]
    s1 = sum(&parSum, data)
    sys.out.println("SUM: " ++ Any.toString(s1))
    s2 = sum(&seqSum, data)
    sys.out.println("SUM: " ++ Any.toString(s2))
