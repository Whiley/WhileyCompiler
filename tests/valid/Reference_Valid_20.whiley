type Node is {
    int nodeType,
    ...
}
type Element is {
    int nodeType,
    int nodeValue
}
public export method test():
  &Element p_elem = new {nodeType:1, nodeValue:2}
  &Node p_node = p_elem
  //
  assert p_node->nodeType == 1