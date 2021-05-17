import React from "react";
import Connection from "../API/Connection";


class UpdateQuantity extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            quantity: ''
        }

        this.handleQuantityChange = this.handleQuantityChange.bind(this);
        this.handleQuantityUpdate = this.handleQuantityUpdate.bind(this);
        this.handleResponse = this.handleResponse.bind(this);
    }

    handleQuantityChange(event){
        this.setState({quantity: event.target.value});
    }

    handleResponse(result){
        if(!result.response.isFailure){
            alert("quantity updated successfully");
            this.props.handler();
        }
        else{
            alert(result.response.errMsg);
        }
        this.setState({quantity: ''})
    }

    handleQuantityUpdate(){
        Connection.sendUpdateProductQuantity(this.props.storeID, this.props.productID, this.state.quantity).then(this.handleResponse, Connection.handleReject);
    }

    render() {
        return (
            <div>
                <form>
                    <input type="text" name="quantity" placeholder="New quantity" value={this.state.quantity}
                              onChange={this.handleQuantityChange}/>
                </form>
                <button onClick={this.handleQuantityUpdate}>Update quantity</button>
            </div>
        );
    }
}

export default UpdateQuantity;