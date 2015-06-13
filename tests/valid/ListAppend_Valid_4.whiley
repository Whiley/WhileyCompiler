function append([int] input) -> [int]:
    [int] rs = []
    int i = 0
    while i < |input|:
        rs = [input[i]] ++ rs
        i = i + 1
    return rs

public export method test() -> void:
    [int] xs = append("abcdefghijklmnopqrstuvwxyz")
    assume xs == [
        'z',
        'y',
        'x',
        'w',
        'v',
        'u',
        't',
        's',
        'r',
        'q',
        'p',
        'o',
        'n',
        'm',
        'l',
        'k',
        'j',
        'i',
        'h',
        'g',
        'f',
        'e',
        'd',
        'c',
        'b',
        'a'
    ]
