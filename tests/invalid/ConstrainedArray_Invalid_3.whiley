type char is (int x) where x >= 0 && x <= 255
type string is char[]

function update(string str) -> char[]:
    return [-1]

public export method test():
    string s1 = "Hello World"
    assume update(s1) == [-1]
