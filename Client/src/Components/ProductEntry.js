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
              <h3>sold by: {this.props.seller}</h3>
              <p>
                  rating {this.props.rating} stars by {this.props.numReview} reviewers
              </p>
              <p>
                  categories: {this.props.categories}
              </p>
          </div>
        );
    }
}

export default ProductEntry;