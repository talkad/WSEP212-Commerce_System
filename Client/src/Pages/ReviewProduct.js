import React from "react";
import Connection from "../API/Connection";

class ReviewProduct extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            review: ''
        }

        this.handleReviewChange = this.handleReviewChange.bind(this);
        this.handleReviewSubmit = this.handleReviewSubmit.bind(this);
        this.handleResponse = this.handleResponse.bind(this);
    }

    handleReviewChange(event){
        this.setState({review: event.target.value});
    }

    handleResponse(result){
        if(!result.response.isFailure){
            alert("product reviewed successfully");
            this.setState({review: ''});
            this.props.submitHandler();
        }
        else{
            alert(result.response.errMsg);
        }
    }

    handleReviewSubmit(){
        Connection.sendAddProductReview(this.props.storeID, this.props.productID, this.state.review).then(this.handleResponse, Connection.handleReject);
    }

    render() {
        return (
            <div>
                <form>
                    <textarea name="review" placeholder="Write a review" value={this.state.review}
                          onChange={this.handleReviewChange}/>
                </form>
                <button onClick={this.handleReviewSubmit}>Submit Review</button>
            </div>
        );
    }

}

export default ReviewProduct;