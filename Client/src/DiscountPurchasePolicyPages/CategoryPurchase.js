import React from 'react';
import StaticUserInfo from "../API/StaticUserInfo";
import Connection from "../API/Connection";
import {Col, Form} from 'react-bootstrap';
class CategoryPurchase extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'addPurchaseRule',
            username: window.sessionStorage.getItem('username'),
            storeId: window.sessionStorage.getItem('storeID')(),
            type: 'CategoryPurchaseRule',
            category: '',
            minUnits: '',
            maxUnits: '',
        };
    }


    handleInputChange = (e, name) => {
        this.setState({
            [name]: e.target.value
        })
    }

    handleClick(e) {
        e.preventDefault();
        Connection.sendCategoryPurchaseRule(this.state.functionName, this.state.username, this.state.storeId, this.state.type, this.state.category, this.state.minUnits, this.state.maxUnits).then(this.handleAddCategoryDiscountResponse, Connection.handleReject)
    }

    handleAddCategoryDiscountResponse(result){
        if(!result.isFailure){
            alert("adding purchase success");
        }
        else{
            alert(result.errMsg);
        }
    }

    render() {
        return (
            <form>
                <h1>Add Category Purchase Rule Page</h1>
                <div> <label> Username : <input readOnly value = {this.state.username} className = "username" type = "text" onChange = {(e) => this.handleInputChange(e, 'username')}/> </label> </div>
                <div> <label> Store Id : <input readOnly value = {this.state.storeId} className = "storeId" type = "text" onChange = {(e) => this.handleInputChange(e, 'storeId')}/> </label> </div>
                <div> <label> Category : <input className = "category" type = "text" onChange = {(e) => this.handleInputChange(e, 'category')}/> </label> </div>
                <div> <label> Minimum Units : <input className = "minUnits" type = "text"   onChange = {(e) => this.handleInputChange(e, 'minUnits')}/> </label> </div>
                <div> <label> Maximum Units : <input className = "maxUnits" type = "text" onChange = {(e) => this.handleInputChange(e, 'maxUnits')}/> </label> </div>

                <button type = "button" onClick = {(e) => this.handleClick(e)}> Add Purchase Rule </button>
            </form>
        )
    }

}


export default CategoryPurchase;