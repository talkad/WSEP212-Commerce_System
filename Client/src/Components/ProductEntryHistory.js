import React from "react";
import {Button, Card} from "react-bootstrap";
import ReviewProduct from "../Pages/ReviewProduct";

class ProductEntryHistory extends React.Component{
    constructor(props) {
        super(props);

    }

    render() {
        return(
            <div className='search-result-div'>
                <Card border='dark' className='card'>
                    <Card.Body>
                        <Card.Title>{this.props.name}</Card.Title>
                        <Card.Subtitle>Bought for: {this.props.price}</Card.Subtitle>
                        <Card.Text>Sold by: {this.props.storeID}</Card.Text>
                        <Card.Text>Quantity Bought: {this.props.amount}</Card.Text>
                        <ReviewProduct storeID2={this.props.storeID} productID2={this.props.productID}/>
                        {/*<ReviewProduct storeID={this.props.storeID} productID={this.props.productID} submitHandler={() => showReview = false}/>*/}
                        {/*<Button variant="primary" className='card-button' size="sm" onClick={this.props.action_handler}><span style={{fontSize: "small"}}>{this.props.action}</span></Button>*/}
                    </Card.Body>
                </Card>
            </div>
        );
    }
}

export default ProductEntryHistory;