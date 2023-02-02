use whiley_types::{Constraint,INT32,Types,Typing};

type MyTyping = Typing<Types>;

#[test]
fn test_int_01() {
    let mut typing = MyTyping::new(1);
    // Constrain
    typing.constrain(Constraint::LowerBound(0,INT32));
    // Done
    assert!(typing.ok());
}
