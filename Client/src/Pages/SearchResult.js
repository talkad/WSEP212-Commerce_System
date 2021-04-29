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

class SearchResult extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            searchOption: '',
            responseResults: [],
            showStore: false,
            showProduct: false,
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
            this.setState({searchOption: searchOption, showProduct: true});

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
            this.setState({searchOption: searchOption, showStore: true})
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

    handleShowStoreProducts(products){
        this.setState({showStore: false, showProduct: true, responseResult: products});
    }

    render() {
        return(
          <div>
              <h1>Search Results</h1>
              {this.state.showProduct &&
              <ul>
                  {this.state.responseResults.map(({name, productID, storeID, price,
                                                       categories, keywords, reviews, rating, numRatings}) =>(
                      <div>
                          <li>
                              <ProductEntry
                                  name = {name}
                                  price = {price}
                                  categories = {categories}
                                  reviews = {reviews}
                              />
                          </li>
                          <button onClick={() => this.handleAddToCart(productID, storeID)}>add to cart</button>
                      </div>
                  ) ) }
              </ul>
              }
              {this.state.showStore &&
              <ul>
                  {this.state.responseResults.map(({id, name, products}) =>(
                      <div>
                          <li>
                              <StoreEntry
                                  name = {name}
                                  id = {id}
                              />
                          </li>
                          <button onClick={() => this.handleShowStoreProducts(products)}>show store products</button>
                      </div>
                  ) ) }
              </ul>
              }
          </div>
        );
    }
}

export default SearchResult;