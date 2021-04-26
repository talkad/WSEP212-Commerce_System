import React from "react";
import ProductEntry from "../Components/ProductEntry";
import ReviewProduct from "./ReviewProduct";


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
    }


    render() {
        return (
            <div>
                <h1>Purchase History</h1>
                <ul>
                    {products.map(({name, productID, storeID, price, seller,
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
                            <ReviewProduct storeID={storeID} productID={productID} submitHandler={() => showReview = false}/>
                        </div>
                    ) ) }
                </ul>
            </div>
        );
    }

}

export default PurchaseHistory;