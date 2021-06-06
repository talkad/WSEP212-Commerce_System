import React from "react";
import Connection from "../API/Connection";
import {Button, Form, Spinner} from "react-bootstrap";

class ReviewProduct extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            review: '',
            submitted: false,
        }

        this.handleReviewChange = this.handleReviewChange.bind(this);
        this.handleReviewSubmit = this.handleReviewSubmit.bind(this);
        this.handleResponse = this.handleResponse.bind(this);
    }

    handleReviewChange(event){
        this.setState({review: event.target.value});
    }

    handleResponse(result){
        if(!result.isFailure){
            alert("product reviewed successfully");
            this.setState({review: '', submitted: false});
            this.props.continueHandler();
        }
        else{
            alert(result.errMsg);
            this.setState({review: '', submitted: false});
        }
    }

    handleReviewSubmit(){
        Connection.sendAddProductReview(this.props.storeID, this.props.productID, this.state.review).then(this.handleResponse, Connection.handleReject);
        this.setState({submitted: true})
    }

    render() {
        return (
            <div id="review_modal">
                <h1>Reviewing {this.props.product}</h1>
                <h2>From {this.props.store}</h2>
                <br/>
                <Form.Control onChange={this.handleReviewChange} as="textarea" rows={3} />
                {!this.state.submitted &&
                <Button onClick={this.handleReviewSubmit} style={{marginTop: "5px"}}>
                    Submit Review
                </Button>}

                {this.state.submitted &&
                <Button style={{marginTop: "5px"}} disabled>
                    <Spinner animation="border" size="sm"/>
                </Button>}
            </div>
        );
    }

}

export default ReviewProduct;