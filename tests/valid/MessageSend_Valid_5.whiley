import whiley.lang.System

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

method main(System.Console sys) => void:
    data = [1, 3, 5, 7, 3, 198, 1, 4, 6]
    sum = create(data)
    sum.start()
    r = sum.get()
    sys.out.println("SUM: " ++ Any.toString(r))
