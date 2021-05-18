import React from "react";
import ProductEntry from "../Components/ProductEntry";
import ReviewProduct from "./ReviewProduct";
import Connection from "../API/Connection";
import ProductEntryHistory from "../Components/ProductEntryHistory";


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

class PurchaseHistory extends React.Component{

    constructor(props) {
        super(props);

        this.state = {
            purchaseHistory: products,
        }

        this.handleResponse = this.handleResponse.bind(this);
    }

    handleResponse(result){
        if(!result.response.isFailure){
            this.setState({purchaseHistory: result.response.result});
        }
        else{
            alert(result.response.errMsg);
            //this.props.history.goBack();
            // this.state.history.goBack();
        }
    }

    componentDidMount() {
        Connection.sendGetPurchaseHistory().then(this.handleResponse, Connection.handleReject);
    }

    render() {
        return (
            <div>
                <h1>Purchase History</h1>
                    {this.state.purchaseHistory.map(({name, productID, storeID, price, seller,
                                       categories, rating, numReview, showReview}) =>(
                        <div>
                                <ProductEntryHistory
                                    name = {name}
                                    price = {price}
                                    seller = {seller}
                                    productID = {productID}
                                    storeID = {storeID}
                                />
                        </div>
                    ) ) }
            </div>
        );
    }
}

export default PurchaseHistory;