
function sum([int] xs) -> [int]:
    [int] rs = []
    for x in xs where |rs| <= 2:
        rs = rs ++ [x]
    return rs
