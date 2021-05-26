import React from "react";
import {Button, Card, Form, FormControl, InputGroup} from "react-bootstrap";
import UpdateQuantity from "./UpdateQuantity";
import * as Icon from 'react-bootstrap-icons';

class ProductEntryCart extends React.Component{
    constructor(props) {
        super(props);

    }

    render() {
        return(
            <div className='cart_card_block'>
                <Card border='light' className='card' style={{width: '100%'}}>
                    <Card.Body>
                        <div id="cart_card_info">
                            <Card.Title>{this.props.name}</Card.Title>
                            <br/>
                            <Card.Subtitle>Seller: {this.props.seller}</Card.Subtitle>
                            <br/>
                            <Card.Subtitle>Price: {this.props.price}</Card.Subtitle>
                        </div>
                        <div id="cart_card_actions">
                            <InputGroup style={{width: '50%'}}>
                                <InputGroup.Prepend>
                                    <Button variant="outline-secondary">-</Button>
                                </InputGroup.Prepend>

                                <Form.Control style={{textAlign: 'center'}} type="text" value={this.props.amount} required/>

                                <InputGroup.Append>
                                    <Button variant="outline-secondary">+</Button>
                                </InputGroup.Append>
                            </InputGroup>
                            <br/>
                            <Icon.Trash onClick={this.props.handlerRemove}/>
                            {/*<button onClick={this.props.handlerRemove}></button>*/}
                        </div>
                    </Card.Body>
                </Card>

                {/*<Card border='dark' className='card'>*/}
                {/*    <Card.Body>*/}
                {/*        <Card.Title>{this.props.name}</Card.Title>*/}
                {/*        <Card.Subtitle>price: {this.props.price}</Card.Subtitle>*/}
                {/*        <Card.Text>Amount: {this.props.amount}</Card.Text>*/}
                {/*        <UpdateQuantity storeID={this.props.storeID} productID={this.props.productID} handler={this.props.handler}/>*/}
                {/*        <button onClick={this.props.handlerRemove}>Remove from cart</button>*/}
                {/*    </Card.Body>*/}
                {/*</Card>*/}
            </div>
        );
    }
}

export default ProductEntryCart;