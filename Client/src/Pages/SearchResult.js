import React from "react";
import ProductEntry from '../Components/ProductEntry'
import StoreEntry from "../Components/StoreEntry";

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

class SearchResult extends React.Component{
    constructor(props) {
        super(props);
    }


    handleAddToCart(productID, storeID){
        // TODO: send to server add to cart command
        console.log(productID, storeID);
    }

    render() {
        return(
          <div>
              <h1>Search Results</h1>
              <ul>
                  {products.map(({name, productID, storeID, price, seller,
                                     categories, rating, numReview}) =>(
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
                          <button onClick={() => this.handleAddToCart(productID, storeID)}>add to cart</button>
                      </div>
                  ) ) }
              </ul>
          </div>
        );
    }
}

export default SearchResult;