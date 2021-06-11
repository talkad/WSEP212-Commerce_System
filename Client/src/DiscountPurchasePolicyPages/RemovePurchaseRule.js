import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";
import Connection from "../API/Connection";


class RemovePurchaseRule extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'removePurchaseRule',
            username: window.sessionStorage.getItem('username'),
            storeId: window.sessionStorage.getItem('storeID')(),
            purchaseRuleID: '',
        };

        this.handleClick = this.handleClick.bind(this);
    }

    handleClick(e) {
        e.preventDefault();
        Connection.sendRemovePurchase(this.state.functionName, this.state.username, this.state.storeId, this.state.purchaseRuleID).then(this.handleResponse, Connection.handleReject)
    }

    handleResponse(result){
        if(!result.isFailure){
            alert("deleting purchase successful");
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
                <div> <label> Purchase ID : <input className = "purchaseRuleID" type = "text" onChange = {(e) => this.handleInputChange(e, 'purchaseRuleID')}/> </label> </div>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Delete Purchase </button>
            </form>
        )
    }
}

export default RemovePurchaseRule;