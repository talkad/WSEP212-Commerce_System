import React from "react";
import {Button, Card, Form, FormControl, InputGroup} from "react-bootstrap";
import UpdateQuantity from "./UpdateQuantity";
import * as Icon from 'react-bootstrap-icons';
import Connection from "../API/Connection";

class ProductEntryCart extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            original_amount: this.props.amount,
            amount: this.props.amount,
            showUpdate: false,
        }

        this.quantityChange = this.quantityChange.bind(this);
        this.updateQuantity = this.updateQuantity.bind(this);
    }

    isNumber(evt) {
        let input = evt.target.value;

        for(let i=0; i<input.length; i++){
            if(input[i] < '0' || input[i] > '9'){
                return false;
            }
        }

        return true;
    }

    quantityChange(event){
        if(this.isNumber(event)) {
            if (parseInt(event.target.value) !== this.state.original_amount) {
                this.setState({amount: event.target.value, showUpdate: true});
            } else {
                this.setState({amount: event.target.value, showUpdate: false});
            }
        }
    }

    updateQuantity(){
        if(this.state.amount !== ''){
            Connection.sendUpdateProductQuantity(this.props.storeID, this.props.productID, this.state.amount).then(this.props.handlerUpdate, Connection.handleReject);
        }
        else{
            alert("please enter a quantity")
        }
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

                                <Form.Group>
                                    <Form.Label>Qty</Form.Label>
                                    <Form.Control onChange={this.quantityChange} style={{textAlign: 'center'}} type="text" value={this.state.amount}/>
                                    {this.state.showUpdate && <Button onClick={this.updateQuantity} style={{marginTop: "3px"}}>update</Button>}
                                </Form.Group>

                            </InputGroup>
                            <br/>
                            <Icon.Trash onClick={this.props.handlerRemove}/>
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