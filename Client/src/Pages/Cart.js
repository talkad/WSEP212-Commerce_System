import React from "react";
import ProductEntry from "../Components/ProductEntry";
import {Link} from "react-router-dom";
import UpdateQuantity from "../Components/UpdateQuantity";
import Connection from "../API/Connection";
import ProductEntryCart from "../Components/ProductEntryCart";
import {CardGroup, Spinner} from "react-bootstrap";

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
            loaded: false
        }

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

    render() {
        const zip = (a, b) => a.map((k, i) => [k, b[i]]);
        return (
            <div>
                <h1>Cart</h1>
                <p><Link to="/checkout">Checkout</Link></p>
                {!this.state.loaded && <Spinner animation="grow"/>}
                {this.state.loaded && this.state.cart.map(({storeID, storeName, productsDTO, amounts}) => (
                    <div>
                        <h2>{storeName}</h2>
                        <CardGroup>
                            {zip(productsDTO, amounts).map( entry => (
                                <ProductEntryCart
                                    name = {entry[0].name}
                                    price = {entry[0].price}
                                    storeID = {storeID}
                                    amount = {entry[1]}
                                    productID = {entry[0].productID}
                                    handler = {() => this.handleQuantityChange()}
                                    handlerRemove = {() => this.handleRemoveFromCart(storeID, entry[0].productID)}
                                />
                            ) ) }
                        </CardGroup>
                    </div>
                    // <div>
                    //     <h1>{storeID}</h1>
                    //     <h1>{storeName}</h1>
                    //     {/*<h1>{productsDTO}</h1>*/}
                    //     {/*<h1>{amounts}</h1>*/}
                    // </div>
                ))}
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