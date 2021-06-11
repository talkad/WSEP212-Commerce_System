import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";
import Connection from "../API/Connection";


class RemoveDiscountRule extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'removeDiscountRule',
            username: window.sessionStorage.getItem('username'),
            storeId: window.sessionStorage.getItem('storeID')(),
            discountRuleID: '',
        };

        this.handleClick = this.handleClick.bind(this);
    }

    handleClick(e) {
        e.preventDefault();
        Connection.sendRemoveDiscount(this.state.functionName, this.state.username, this.state.storeId, this.state.discountRuleID).then(this.handleResponse, Connection.handleReject)
    }

    handleResponse(result){
        if(!result.isFailure){
            alert("deleting discount successful");
        }
        else{
            alert(result.errMsg);
        }
    }

    handleInputChange = (e, name) => {
        this.setState({
            [name]: e.target.value
        })
    }

    render() {
        return (
            <form>
                <h1>Delete Discount Page </h1>
                <div> <label> Username : <input readOnly value = {this.state.username} className = "username" type = "text" onChange = {(e) => this.handleInputChange(e, 'username')}/> </label> </div>
                <div> <label> Store Id : <input readOnly value = {this.state.storeId} className = "storeId" type = "text" onChange = {(e) => this.handleInputChange(e, 'storeId')}/> </label> </div>
                <div> <label> Discount ID : <input className = "discountRuleID" type = "text" onChange = {(e) => this.handleInputChange(e, 'discountRuleID')}/> </label> </div>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Delete Discount </button>
            </form>
        )
    }
}

export default RemoveDiscountRule;