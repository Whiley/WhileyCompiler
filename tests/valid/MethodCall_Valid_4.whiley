type Sum is &{int result, int[] items}

method start(Sum this) :
    int sum = 0
    int i = 0
    int[] items = this->items
    while i < |items| where i >= 0:
        sum = sum + items[i]
        i = i + 1
    this->result = sum

method get(Sum this) -> int:
    return this->result

method create(int[] items) -> Sum:
    return new {result: 0, items: items}

method seqSum(int[] items) -> int:
    int r = 0
    int i = 0
    while i < |items| where i >= 0:
        r = r + items[i]
        i = i + 1
    return r

method parSum(int[] items) -> int:
    Sum sum = create(items)
    start(sum)
    return get(sum)

type pst is method (int[])->int

method sum(pst m, int[] data) -> int:
    return m(data)

public export method test() :
    int[] data = [1, 3, 5, 7, 3, 93, 1, 4, 6]
    int s1 = sum(&parSum, data)
    assume s1 == 123
    int s2 = sum(&seqSum, data)
    assume s1 == 123
