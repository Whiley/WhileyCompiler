define LinkedList as int | (LinkedList next, int data)

define posLink as (posList next, nat data)
define posList as int | posLink

posList f(LinkedList list):
    return list
