import React from "react";

class ProductEntry extends React.Component{
    constructor(props) {
        super(props);

    }


    // TODO: read somewhere not use headers like that
    render() {
        return(
          <div>
              <h2>{this.props.name}</h2>
              <h3>price: {this.props.price}</h3>
              <p>
                  categories: {this.props.categories}
              </p>
              <p>
                  reviews: {this.props.reviews}
              </p>
          </div>
        );
    }
}

export default ProductEntry;