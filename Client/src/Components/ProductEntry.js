import React from "react";
import {Button, Card} from "react-bootstrap";

class ProductEntry extends React.Component{
    constructor(props) {
        super(props);

    }

    render() {
        return(
          <div className='search-result-div'>
              <Card border='dark' className='card'>
                 <Card.Body>
                     <Card.Title>{this.props.name}</Card.Title>
                     <Card.Subtitle>price: {this.props.price} ₪</Card.Subtitle>
                     <Card.Text>Sold by: {this.props.storeID}</Card.Text>
                     <Button variant="primary" className='card-button' size="sm" onClick={this.props.action_handler}><span style={{fontSize: "small"}}>{this.props.action}</span></Button>
                 </Card.Body>
              </Card>
          </div>
        );
    }
}

export default ProductEntry;