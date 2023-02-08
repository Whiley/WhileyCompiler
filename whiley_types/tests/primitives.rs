use whiley_types::{INT32,Types,Typing};

type MyTyping = Typing<Types>;

#[test]
fn test_int_01() {
    let mut typing = MyTyping::new();
    // Allocate a fresh variable
    let var = typing.fresh_var();
    // Constrain
    typing.constrain_above(var,INT32);
    // Done
    assert!(typing.ok());
}
