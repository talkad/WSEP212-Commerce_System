import React from "react";

class Search extends React.Component{

    constructor(props) {
        super(props);

        this.state = {
            searchOption: 'N/A',
            searchProductBy: 'N/A',
            freeText: '',
        }

        this.searchOptionChangeHandler = this.searchOptionChangeHandler.bind(this);
        this.searchProductByChangeHandler = this.searchProductByChangeHandler.bind(this);
        this.searchOption = this.searchOption.bind(this);
        this.handleFreeTextChange = this.handleFreeTextChange.bind(this);
    }

    searchOptionChangeHandler(event) {
        this.setState({ searchOption: event.target.value });
    }

    searchProductByChangeHandler(event) {
        this.setState( {searchProductBy: event.target.value });
    }

    searchOption(){
        if(this.state.searchOption === "Product"){
            return <div>
                <div onChange={this.searchProductByChangeHandler}>
                    <label>Search product by:</label>
                    <input type="radio" value="Name" name="searchProductBy" /> By Name
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
        //TODO: sending to the server the appropriate request and moving to a screen which show the result
    }

    render() {
        return (
            <div className="Search">
                <div onChange={this.searchOptionChangeHandler}>
                    <label>Search for:</label>
                    <input type="radio" value="Store" name="searchOption" /> Store
                    <input type="radio" value="Product" name="searchOption" /> Product
                </div>
                {this.searchOption()}
                <div>
                    <form>
                        <input type="text" name="searchText" placeholder="Search for anything" value={this.state.freeText}
                               onChange={this.handleFreeTextChange}/>
                        <button onClick={this.handleSearch}>Search</button>
                    </form>
                </div>
            </div>
        );
    }
}



export default Search;