import React from "react";

class ReviewProduct extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            review: ''
        }

        this.handleReviewChange = this.handleReviewChange.bind(this);
        this.handleReviewSubmit = this.handleReviewSubmit.bind(this);
    }

    handleReviewChange(event){
        this.setState({review: event.target.value});
    }

    handleReviewSubmit(){
        // TODO: send review to server
        this.setState({review: ''})
        this.props.submitHandler();
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