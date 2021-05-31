import React from "react";
import ProductEntry from "../Components/ProductEntry";
import {Link} from "react-router-dom";
import UpdateQuantity from "../Components/UpdateQuantity";
import Connection from "../API/Connection";
import ProductEntryCart from "../Components/ProductEntryCart";
import {Button, Card, CardGroup, Image, ListGroup, Spinner} from "react-bootstrap";
import empty_cart from '../Images/harold_cart_empty.png'

const products = [
        {
            name: "brioche",
            productID: 1,
            storeID: 1,
            price: 50.5,
            seller: "ma'afia",
            categories: ["pastry", "tasty"],
            rating: 5,
            numReview: 200,
        },
        {
            name: "eclair",
            productID: 2,
            storeID: 2,
            price: 50.5,
            seller: "ma'afia2",
            categories: ["pastry", "tasty"],
            rating: 4.5,
            numReview: 300,
        }
    ]

class Cart extends React.Component { //TODO check if the cart is empty and show an appropriate image
    constructor(props) {
        super(props);

        this.state = {
            cart: [],
            loaded: false,

        }

        this.totalAmount =  0;
        this.handleRemoveFromCart = this.handleRemoveFromCart.bind(this);
        this.handleQuantityChange = this.handleQuantityChange.bind(this);
        this.handleGetCartDetailsResponse = this.handleGetCartDetailsResponse.bind(this);
    }

    handleGetCartDetailsResponse(result) {
        if (!result.isFailure) {
            this.setState({cart: result.result, loaded: true});
        } else {
            alert(result.errMsg);
            this.props.history.goBack();
        }
    }

    componentDidMount() {
        this.totalAmount = 0;
        Connection.sendGetCartDetails().then(this.handleGetCartDetailsResponse, Connection.handleReject);
    }


    handleRemoveFromCartResponse(result) {
        if (!result.isFailure) {
            window.location.reload();
            // Connection.sendGetCartDetails().then(this.handleGetCartDetailsResponse, Connection.handleReject);
        } else {
            alert(result.errMsg);
        }
    }

    handleRemoveFromCart(storeID, productID) {
        Connection.sendRemoveFromCart(storeID, productID).then(this.handleRemoveFromCartResponse, Connection.handleReject);
    }

    handleQuantityChange() {
        window.location.reload();
    }

    addToTotal(price, amount, toZero){

        if(!toZero){
            let price_value = parseInt(price);
            let amount_value = parseInt(amount);

            this.totalAmount = this.totalAmount + price_value * amount_value;
        }
        else{
            this.totalAmount = 0;
        }

        return true;
    }

    render() {
        const zip = (a, b) => a.map((k, i) => [k, b[i]]);
        return (
            <div id="cart_page">
                <div id="cart_title">
                    <h1>Shopping cart</h1>
                </div>
                {this.state.loaded && (this.state.cart.length === 0) &&
                <Image src={empty_cart}/>}
                {/*{this.state.loaded && this.state.cart.length !== 0 && <p><Link to="/checkout">Checkout</Link></p>}*/}
                {!this.state.loaded && <Spinner animation="grow"/>}
                {this.state.loaded && (this.state.cart.length !== 0) && <div id="card_page_content">
                    <div id="cart_items">
                        <ListGroup>
                        {this.addToTotal(0, 0, true) && this.state.cart.map(({storeID, storeName, productsDTO, amounts}) => (
                            <div>
                                {zip(productsDTO, amounts).map( entry => (this.addToTotal(entry[0].price, entry[1], false) &&
                                    <ListGroup.Item><ProductEntryCart
                                        name = {entry[0].name}
                                        price = {entry[0].price}
                                        storeID = {storeID}
                                        amount = {entry[1]}
                                        seller = {storeName}
                                        productID = {entry[0].productID}
                                        handlerUpdate = {() => this.handleQuantityChange()}
                                        handlerRemove = {() => this.handleRemoveFromCart(storeID, entry[0].productID)}
                                    />
                                    </ListGroup.Item>
                                ) ) }
                            </div>
                        ))}
                        </ListGroup>
                    </div>
                    <div id="cart_checkout">
                        <Card>
                            <Card.Title>Total amount</Card.Title>
                            <Card.Text>Products total: {this.totalAmount}</Card.Text>
                            <Card.Text>Shipping: free ;)</Card.Text>
                            <hr/>
                            <Card.Subtitle>Total amount: {this.totalAmount}</Card.Subtitle>
                            <Link to="/checkout"><Button style={{marginTop: '10px', marginBottom: '5px'}}>Checkout</Button></Link>
                        </Card>
                    </div>
                </div>}
                {/*{this.state.loaded && this.state.cart.map(({name, productID, storeID, price,*/}
                {/*                                               categories, keywords, reviews, rating, numRatings}) =>(*/}
                {/*    <div>*/}

                {/*    </div>*/}
                {/*) ) }*/}
            </div>
        );
    }
}

export default Cart;