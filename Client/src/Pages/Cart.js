import React from "react";
import ProductEntry from "../Components/ProductEntry";
import {Link} from "react-router-dom";
import UpdateQuantity from "../Components/UpdateQuantity";
import Connection from "../API/Connection";

// const products = [
//     {
//         name: "brioche",
//         productID: 1,
//         storeID: 1,
//         price: 50.5,
//         seller: "ma'afia",
//         categories: ["pastry", "tasty"],
//         rating: 5,
//         numReview: 200,
//     },
//     {
//         name: "eclair",
//         productID: 2,
//         storeID: 2,
//         price: 50.5,
//         seller: "ma'afia2",
//         categories: ["pastry", "tasty"],
//         rating: 4.5,
//         numReview: 300,
//     }
//     ]

class Cart extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            cart: [],
        }

        this.handleRemoveFromCart = this.handleRemoveFromCart.bind(this);
        this.handleQuantityChange = this.handleQuantityChange.bind(this);
        this.handleGetCartDetailsResponse = this.handleGetCartDetailsResponse.bind(this);
    }

    handleGetCartDetailsResponse(result){
        if(!result.response.isFailure){
            this.setState({cart: result.response.result});
        }
        else{
            alert(result.response.errMsg);
            this.props.history.goBack();
        }
    }

    componentDidMount() {
        Connection.sendGetCartDetails().then(this.handleGetCartDetailsResponse, Connection.handleReject);
    }

    handleRemoveFromCartResponse(result){
        if(!result.response.isFailure){
            Connection.sendGetCartDetails().then(this.handleGetCartDetailsResponse, Connection.handleReject);
        }
        else{
            alert(result.response.errMsg);
        }
    }

    handleRemoveFromCart(storeID, productID){
        Connection.sendRemoveFromCart(storeID, productID).then(this.handleRemoveFromCart, Connection.handleReject);
    }


    handleQuantityChange(){
        Connection.sendGetCartDetails().then(this.handleGetCartDetailsResponse, Connection.handleReject);
    }

    render() {
        return (
            <div>
                <h1>Cart</h1>
                <p><Link to="/checkout">Checkout</Link></p>
                <ul>
                    {this.state.cart.map(({name, productID, storeID, price, seller,
                                       categories, rating, numReview, showReview}) =>(
                        <div>
                            <li>
                                <ProductEntry
                                    name = {name}
                                    price = {price}
                                    seller = {seller}
                                    categories = {categories}
                                    rating = {rating}
                                    numReview = {numReview}
                                />
                            </li>
                            <UpdateQuantity storeID={storeID} productID={productID} handler={() => this.handleQuantityChange()}/>
                            <button onClick={this.handleRemoveFromCart}>Remove from cart</button>
                        </div>
                    ) ) }
                </ul>
            </div>
        );
    }
}

export default Cart;