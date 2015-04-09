import whiley.lang.*

method main(System.Console sys) -> void:
    {bool flag, int code} x = {flag: true, code: 0}
    assert x == {flag: true, code: 0}
    x.flag = false
    assert x == {flag: false, code: 0}    
