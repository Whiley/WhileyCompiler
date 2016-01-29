

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

public export method test() :
    int[] data = [1, 3, 5, 7, 3, 198, 1, 4, 6]
    Sum sum = create(data)
    start(sum)
    assume get(sum) == 228
