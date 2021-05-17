import React from "react";
import Connection from "../API/Connection";
import {Container, Form, FormControl, InputGroup, Button} from "react-bootstrap";

class Search extends React.Component{

    constructor(props) {
        super(props);

        this.state = {
            searchOption: "product",
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
        if(this.state.searchOption === "product"){
            return <div>
                <div onChange={this.searchProductByChangeHandler}>
                    <label>Search product by:</label>
                    <input type="radio" value="name" name="searchProductBy" defaultChecked /> By Name
                    <input type="radio" value="category" name="searchProductBy" /> By Category
                    <input type="radio" value="keyword" name="searchProductBy" /> By Keyword
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
                <form autoComplete="on" action="/search/" method="get" className="form-search">
                    <div onChange={this.searchOptionChangeHandler}>
                        <label>Search for:</label>
                        <input className='search-radio' type="radio" value="product" name="searchOption" defaultChecked /> Product
                        <input className='search-radio' type="radio" value="store" name="searchOption" /> Store
                    </div>
                    {this.searchProductType()}
                    <InputGroup className="mb-3">
                        <FormControl
                            name='free-text'
                            placeholder="Search for anything..."
                            aria-label="Free search"
                            aria-describedby="basic-addon2"
                        />
                        <InputGroup.Append>
                            <Button type='submit' variant="outline-secondary">Button</Button>
                        </InputGroup.Append>
                    </InputGroup>
                </form>
            </div>
        );
    }
}



export default Search;