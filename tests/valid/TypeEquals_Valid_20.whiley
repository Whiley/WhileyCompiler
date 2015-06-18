

function f([(int, any)] xs) -> int:
    if xs is [(int, [int])]:
        return 1
    else:
        return -1

public export method test() -> void:
    [(int,any)] s1 = [(1, "Hello")]
    [(int,any)] s2 = [(1, "Hello"), (1, "World")]
    [(int,any)] s3 = [(1, "Hello"), (2, "Hello")]
    [(int,any)] s4 = [(1, 1), (2, 2)]
    [(int,any)] s5 = [(1, 1), (2, "Hello")]
    assume f(s1) == 1
    assume f(s2) == 1
    assume f(s3) == 1
    assume f(s4) == -1
    assume f(s5) == -1
