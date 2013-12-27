

define fr6nat as int where $ >= 0

int g({fr6nat} xs) ensures $ > 1:
    r = 1
    for y in xs where r > 0:
        r = r + 1
    return r
