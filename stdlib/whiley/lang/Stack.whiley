package whiley.lang

public int top([int] list) requires |list| > 0:
    return list[|list|-1]

public [int] push([int] list, int element) ensures |$| == |list| + 1:
    return list + [element]

public [int] pop([int] list) requires |list| > 0, ensures |$| == |list| - 1:
    end = |list| - 1
    return list[0..end]
