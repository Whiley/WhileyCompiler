function append([int] input) -> [int]:
    [int] rs = []
    int i = 0
    while i < |input|:
        rs = rs ++ [input[i]]
        i = i + 1
    return rs

public export method test() -> void:
    [int] xs = append("abcdefghijklmnopqrstuvwxyz")
    assume xs == [
            'a',
            'b',
            'c',
            'd',
            'e',
            'f',
            'g',
            'h',
            'i',
            'j',
            'k',
            'l',
            'm',
            'n',
            'o',
            'p',
            'q',
            'r',
            's',
            't',
            'u',
            'v',
            'w',
            'x',
            'y',
            'z'
            ]

