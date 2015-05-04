

function f(int x) -> {int=>int}:
    return {1=>x, 3=>2}

function get(int i, {int=>int} map) -> int:
    return map[i]

public export method test() -> void:
    {int=>int} m1 = f(1)
    {int=>int} m2 = f(2)
    {int=>int} m3 = f(3)
    m1[2] = 4
    m2[1] = 23498
    assume get(1, m1) == 1
    assume get(2, m1) == 4  
    assume get(1, m2) == 23498
    assume get(1, m3) == 3
    assume get(3, m3) == 2
