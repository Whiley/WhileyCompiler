

function run(int n, int x) -> bool:
    bool solution = true
    for i in 0 .. n:
        if i == x:
            solution = false
            break
    return solution

public export method test() -> void:
    bool b1 = run(10, 4)
    assume b1 == false
    bool b2 = run(10, -1)
    assume b2 == true
    bool b3 = run(10, 11)
    assume b3 == true
