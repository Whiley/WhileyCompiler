

type Link is {int data}
type BigLink is {int data, int code}

function sum(Link l) -> int:
    return l.data

function sum2(BigLink l) -> int:
  return sum((Link) l)

public export method test():
    BigLink l = {data: 1, code: 'c'}
    assume sum2(l) == 1
