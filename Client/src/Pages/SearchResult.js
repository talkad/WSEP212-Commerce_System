import React from "react";
import ProductEntry from '../Components/ProductEntry'
import StoreEntry from "../Components/StoreEntry";
import Connection from "../API/Connection";
import {Button, Col, Container, Row, Spinner} from "react-bootstrap";

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
    },
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
    },
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
    },
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
    },
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
    },
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
    },
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
    },
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
    },
]

const products1 = [
    {
        name: "brioche",
        productID: 1,
        storeID: 1,
        price: 100,
        seller: "ma'afia",
        categories: ["pastry", "tasty"],
        rating: 5,
        numReview: 200,
    },
    {
        name: "eclair",
        productID: 2,
        storeID: 1,
        price: 100,
        seller: "ma'afia",
        categories: ["pastry", "tasty"],
        rating: 4.5,
        numReview: 300,
    }
]

const products2 = [
    {
        name: "brioche",
        productID: 3,
        storeID: 2,
        price: 50.5,
        seller: "ma'afia2323",
        categories: ["pastry", "tasty"],
        rating: 5,
        numReview: 200,
    },
    {
        name: "eclair",
        productID: 4,
        storeID: 2,
        price: 50.5,
        seller: "ma'afia2323",
        categories: ["pastry", "tasty"],
        rating: 4.5,
        numReview: 300,
    },

]

const stores = [
    {
        id: 1,
        name: 'hanut1',
        products: products1
    },
    {
        id: 2,
        name: 'hanut2',
        products: products2
    }
]

class SearchResult extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            searchOption: '',
            responseResults: [],
            showStore: false,
            showProduct: false,
            loaded: true,
        }

        this.handleSearchResponse = this.handleSearchResponse.bind(this);
        this.handleAddToCartResponse = this.handleAddToCartResponse.bind(this);
    }

    handleSearchResponse(result){
        if(!result.isFailure){
            this.setState({responseResults: result.result, loaded: false});
        }
        else{
            alert(result.errMsg);
        }
    }

    componentDidMount() {
        const urlParams = new URLSearchParams(window.location.search);
        const searchOption = urlParams.get('searchOption');
        const freeText = urlParams.get("free-text");

        if(searchOption === "product"){
            this.setState({searchOption: searchOption, showProduct: true});

            const searchProductBy = urlParams.get("searchProductBy");

            if(searchProductBy === "name"){
                Connection.sendSearchProductByName(freeText).then(this.handleSearchResponse, Connection.handleReject);
            }
            else if(searchProductBy === "category"){
                Connection.sendSearchProductByCategory(freeText).then(this.handleSearchResponse, Connection.handleReject);
            }
            else{ // keyword
                Connection.sendSearchProductByKeyword(freeText).then(this.handleSearchResponse, Connection.handleReject);
            }

        }
        else{
            this.setState({searchOption: searchOption, showStore: true})
            Connection.sendSearchStoreByName(freeText).then(this.handleSearchResponse, Connection.handleReject);
        }
    }

    handleAddToCartResponse(result){
        if(!result.isFailure){
            alert("product added successfully to cart");
        }
        else{
            alert(result.errMsg);
        }
    }

    handleAddToCart(productID, storeID){
        Connection.sendAddToCart(productID, storeID).then(this.handleAddToCartResponse, Connection.handleReject);
    }

    handleShowStoreProducts(products){
        this.setState({showStore: false, showProduct: true, responseResults: products});
    }

    render() {
        return(
          <div>
              <h1>Search Results</h1>
              {this.state.loaded && <Spinner animation="grow" />}
              {!this.state.loaded && this.state.showProduct && this.state.responseResults.map(({name, productID, storeID, price,
                                                                                                   categories, keywords, reviews, rating, numRatings}) =>(
                      <div>
                          <ProductEntry
                              name = {name}
                              price = {price}
                              storeID = {storeID}
                              categories = {categories}
                              reviews = {reviews}
                              action = {"Add to cart"}
                              action_handler = {() => this.handleAddToCart(productID, storeID)}
                          />
                      </div>
                  ) ) }

              {/*{this.state.showStore &&*/}
              {/*<ul>*/}
              {/*    {this.state.responseResults.map(({id, name, products}) =>(*/}
              {/*        <div>*/}
              {/*            <li>*/}
              {/*                <StoreEntry*/}
              {/*                    name = {name}*/}
              {/*                    id = {id}*/}
              {/*                />*/}
              {/*            </li>*/}
              {/*            <Button onClick={() => this.handleShowStoreProducts(products)}>show store products</Button>*/}
              {/*        </div>*/}
              {/*    ) ) }*/}
              {/*</ul>*/}
              {/*}*/}
          </div>
        );
    }
}

export default SearchResult;