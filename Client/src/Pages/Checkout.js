import React from "react";
import Connection from "../API/Connection";

class Checkout extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            creditCard: '',
            address: '',
        }

        this.handleCreditCardChange = this.handleCreditCardChange.bind(this);
        this.handleAddressChange = this.handleAddressChange.bind(this);
        this.handlePurchase = this.handlePurchase.bind(this);
        this.handleResponse = this.handleResponse.bind(this);
    }

    handleCreditCardChange(event) {
        this.setState({creditCard: event.target.value});
    }

    handleAddressChange(event) {
        this.setState({address: event.target.value});
    }

    handleResponse(result){
        if(!result.response.isFailure){
            alert("thank you for your purchase");
            this.props.history.push("/registered");
        }
        else{
            alert(result.response.errMsg);
        }
    }

    handlePurchase(){
        Connection.sendDirectPurchase(this.state.backAccount, this.state.location).then(this.handleResponse, Connection.handleReject);
    }

    render() {
        return (
            <div>
                <h1>Checkout</h1>
                <form>
                    <input type="text" name="creditCard" placeholder="Credit Card" value={this.state.creditCard}
                           onChange={this.handleCreditCardChange}/>
                </form>
                <form>
                    <input type="text" name="Address" placeholder="Address" value={this.state.address}
                           onChange={this.handleAddressChange}/>
                </form>
                <button onClick={this.handlePurchase}>Purchase</button>
            </div>
        );
    }
}

export default Checkout;