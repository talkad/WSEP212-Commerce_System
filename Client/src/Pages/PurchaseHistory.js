import React from "react";
import ProductEntry from "../Components/ProductEntry";
import ReviewProduct from "./ReviewProduct";
import Connection from "../API/Connection";
import ProductEntryHistory from "../Components/ProductEntryHistory";
import {CardGroup, Image, Spinner} from "react-bootstrap";
import ProductEntryCart from "../Components/ProductEntryCart";
import buy_something from "../Images/harold_buy_something.png"


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

class PurchaseHistory extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            purchaseHistory: [],
            loaded: false,
        }

        this.handleResponse = this.handleResponse.bind(this);
    }

    handleResponse(result) {
        if (!result.isFailure) {
            this.setState({purchaseHistory: result.result, loaded: true});
        } else {
            alert(result.errMsg);
            this.props.history.goBack();
        }
    }

    componentDidMount() {
        Connection.sendGetPurchaseHistory().then(this.handleResponse, Connection.handleReject);
    }

    render() {
        const zip = (a, b) => a.map((k, i) => [k, b[i]]);
        return (
            <div>
                <h1>Purchase History</h1>
                {this.state.loaded && (this.state.purchaseHistory.length === 0) &&
                <Image src={buy_something}/>}
                {!this.state.loaded && <Spinner animation="grow"/>}
                {this.state.loaded && this.state.purchaseHistory.map(({basket, totalPrice, purchaseDate}) => (
                    <div>
                        <h2>Purchase from: {purchaseDate}</h2>
                        <h2>Total price: {totalPrice}</h2>

                        <CardGroup>
                            {zip(basket.productsDTO, basket.amounts).map( entry => (
                                <div>
                                    <ProductEntryHistory
                                        name={entry[0].name}
                                        price={entry[0].price}
                                        seller={basket.storeName}
                                        productID={entry[0].productID}
                                        storeID={basket.storeID}
                                        amount={entry[1]}
                                    />
                                </div>
                            ) ) }
                        </CardGroup>

                    </div>
                ))}
            </div>
        );
    }
}

export default PurchaseHistory;