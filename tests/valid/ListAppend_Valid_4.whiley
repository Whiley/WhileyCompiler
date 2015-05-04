

function append([int] input) -> [int]:
    [int] rs = []
    for i in 0 .. |input|:
        rs = [input[i]] ++ rs
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
