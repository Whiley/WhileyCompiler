

type MyObject is &{int field}

method f(MyObject this, int x) -> void:
    assume x == 1

public export method test() -> void:
    MyObject m = new {field: 1}
    f(m,1)
    
