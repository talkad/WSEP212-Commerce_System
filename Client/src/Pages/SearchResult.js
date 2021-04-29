import React from "react";
import ProductEntry from '../Components/ProductEntry'
import StoreEntry from "../Components/StoreEntry";
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
// ]


//TODO: show stores
class SearchResult extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            searchOption: '',
            responseResults: [],
        }

        this.handleSearchResponse = this.handleSearchResponse.bind(this);
        this.handleAddToCartResponse = this.handleAddToCartResponse.bind(this);
    }

    handleSearchResponse(result){
        if(!result.response.isFailure){
            this.setState({responseResult: result.response.result});
        }
        else{
            alert(result.response.errMsg);
        }
    }

    componentDidMount() {
        const urlParams = new URLSearchParams(window.location.search);
        const searchOption = urlParams.get('searchOption');
        const freeText = urlParams.get("freeText");

        if(searchOption === "Product"){
            this.setState({searchOption: searchOption});

            const searchProductBy = urlParams.get("searchProductBy");

            if(searchProductBy === "Name"){
                Connection.sendSearchProductByName(freeText).then(this.handleSearchResponse, Connection.handleReject);
            }
            else if(searchProductBy === "Category"){
                Connection.sendSearchProductByCategory(freeText).then(this.handleSearchResponse, Connection.handleReject);
            }
            else{ // Keyword
                Connection.sendSearchProductByKeyword(freeText).then(this.handleSearchResponse, Connection.handleReject);
            }

        }
        else{
            this.setState({searchOption: searchOption})
            Connection.sendSearchStoreByName(freeText).then(this.handleSearchResponse, Connection.handleReject);
        }
    }

    handleAddToCartResponse(result){
        if(!result.response.isFailure){
            alert("product added successfully to cart");
        }
        else{
            alert(result.response.errMsg);
        }
    }

    handleAddToCart(productID, storeID){
        Connection.sendAddToCart(productID, storeID).then(this.handleAddToCartResponse, Connection.handleReject);
    }



    render() {
        return(
          <div>
              <h1>Search Results</h1>
              <ul>
                  {this.state.responseResults.map(({name, productID, storeID, price, seller,
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