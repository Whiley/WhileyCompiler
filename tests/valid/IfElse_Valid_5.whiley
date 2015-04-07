import whiley.lang.*

type Record is { int flag }

function getFlag(Record d) -> int:
    if d.flag >= 0:
        int r = 1
        if d.flag > 0:
            return r
    else:
        int r = 0
        return 0
    //
    return -1

method main(System.Console console):
    Record r = {flag: 1}
    console.out.println_s("GOT FLAG: " ++ Any.toString(getFlag(r)))
    r = {flag: 0}
    console.out.println_s("GOT FLAG: " ++ Any.toString(getFlag(r)))
    r = {flag: -1}
    console.out.println_s("GOT FLAG: " ++ Any.toString(getFlag(r)))
