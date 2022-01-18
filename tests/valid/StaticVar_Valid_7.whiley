// Test static variable initialiser order
final Color color = RED
final Color RED = 1
final Color BLUE = 2
final Color GREEN = 3

type Color is (int x) where (x == RED) || (x == BLUE) || (x == GREEN)

public export method test():
    assert color == RED
