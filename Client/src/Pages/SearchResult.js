import React from "react";
import ProductEntry from '../Components/ProductEntry'
import StoreEntry from "../Components/StoreEntry";

class SearchResult extends React.Component{
    constructor(props) {
        super(props);
    }

    render() {
        return(
          <div>
            <h1>{window.location.href}</h1>
          </div>
        );
    }
}

export default SearchResult;