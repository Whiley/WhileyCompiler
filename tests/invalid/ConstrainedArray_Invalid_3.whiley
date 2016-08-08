type char is (int x) where x >= 0 && x <= 255
type string is char[]

public function update(string str) -> char[]:
    return [-1]

public method main() -> string:
    string s1 = "Hello World"
    return update(s1)
