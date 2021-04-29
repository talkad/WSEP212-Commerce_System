import React from "react";
import Connection from "../API/Connection";

class Search extends React.Component{

    constructor(props) {
        super(props);

        this.state = {
            searchOption: "Product",
            searchProductBy: '',
            freeText: '',
        }

        this.searchOptionChangeHandler = this.searchOptionChangeHandler.bind(this);
        this.searchProductByChangeHandler = this.searchProductByChangeHandler.bind(this);
        this.searchProductType = this.searchProductType.bind(this);
        this.handleFreeTextChange = this.handleFreeTextChange.bind(this);
        this.handleSearch = this.handleSearch.bind(this);
    }

    searchOptionChangeHandler(event) {
        this.setState({ searchOption: event.target.value });
    }

    searchProductByChangeHandler(event) {
        this.setState( {searchProductBy: event.target.value });
    }

    // returns a JSX of a radio for choosing a type of search for products if search for product is selected
    searchProductType(){
        if(this.state.searchOption === "Product"){
            return <div>
                <div onChange={this.searchProductByChangeHandler}>
                    <label>Search product by:</label>
                    <input type="radio" value="Name" name="searchProductBy" defaultChecked /> By Name
                    <input type="radio" value="Category" name="searchProductBy" /> By Category
                    <input type="radio" value="Keyword" name="searchProductBy" /> By Keyword
                </div>
            </div>;
        }
    }

    handleFreeTextChange(event){
        this.setState( { freeText: event.target.value });
    }

    handleSearch(){
        // if(this.state.searchOption === "Product"){
        //
        // }
        // else{
        //     Connection.sendSearchStoreByName()
        // }
    }

    render() {
        return (
            <div className="Search">
                    <form autoComplete="on" action="search/" method="get">
                        <div onChange={this.searchOptionChangeHandler}>
                            <label>Search for:</label>
                            <input type="radio" value="Product" name="searchOption" defaultChecked /> Product
                            <input type="radio" value="Store" name="searchOption" /> Store
                        </div>
                        {this.searchProductType()}
                        <input type="text" name="searchText" placeholder="Search for anything" value={this.state.freeText}
                               onChange={this.handleFreeTextChange}/>
                        <button onClick={this.handleSearch}>Search</button>
                    </form>
            </div>
        );
    }
}



export default Search;