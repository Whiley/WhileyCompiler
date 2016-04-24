// missing context lifetime

public export method test():
    method()->(&this:int) m = &(-> this:new 1)
