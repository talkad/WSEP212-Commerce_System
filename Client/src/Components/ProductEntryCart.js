import React from "react";
import {Button, Card} from "react-bootstrap";
import UpdateQuantity from "./UpdateQuantity";

class ProductEntryCart extends React.Component{
    constructor(props) {
        super(props);

    }

    render() {
        return(
            <div className='search-result-div'>
                <Card border='dark' className='card'>
                    <Card.Body>
                        <Card.Title>{this.props.name}</Card.Title>
                        <Card.Subtitle>price: {this.props.price}</Card.Subtitle>
                        <Card.Text>Amount: {this.props.amount}</Card.Text>
                        <UpdateQuantity storeID={this.props.storeID} productID={this.props.productID} handler={this.props.handler}/>
                        <button onClick={this.props.handlerRemove}>Remove from cart</button>
                    </Card.Body>
                </Card>
            </div>
        );
    }
}

export default ProductEntryCart;