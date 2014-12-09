
public function update(string str) -> [char]:
    return [(char) -1]

public function f(char c) -> void:
    debug "" ++ c

public method main(System.Console sys) -> void:
    s1 = "Hello World"
    s1 = update(s1)
    if |s1| > 0:
        f(s1[0])
