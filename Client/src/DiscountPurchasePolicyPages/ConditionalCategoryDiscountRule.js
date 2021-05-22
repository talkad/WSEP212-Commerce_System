import React from 'react';
import Select from 'react-select';
import StaticUserInfo from "../API/StaticUserInfo";
import Connection from "../API/Connection";
//import StaticUserInfo from "../MainPages/StaticUserInfo";


class ConditionalCategoryDiscountRule extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'addDiscountRule',
            username: StaticUserInfo.getUsername(),
            storeId: StaticUserInfo.getStoreId(),
            type: 'ConditionalCategoryDiscountRule',
            category: '',
            discount: '',
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
        Connection.sendCondCategoryDiscountRule(this.state.functionName, this.state.username, this.state.storeId, this.state.type, this.state.category, this.state.discount, this.state.minUnits, this.state.maxUnits).then(this.handleAddCategoryDiscountResponse, Connection.handleReject)
    }

    handleAddCategoryDiscountResponse(result){
        if(!result.isFailure){
            alert("adding discount success");
        }
        else{
            alert(result.errMsg);
        }
    }

    render() {
        return (
            <form>
                <h1>Add Conditional Category Discount Rule Page</h1>
                <div> <label> Username : <input readOnly value = {this.state.username} className = "username" type = "text" onChange = {(e) => this.handleInputChange(e, 'username')}/> </label> </div>
                <div> <label> Store Id : <input readOnly value = {this.state.storeId} className = "storeId" type = "text" onChange = {(e) => this.handleInputChange(e, 'storeId')}/> </label> </div>
                <div> <label> Category : <input className = "category" type = "text" onChange = {(e) => this.handleInputChange(e, 'category')}/> </label> </div>
                <div> <label> Discount : <input className = "discount" type = "text" onChange = {(e) => this.handleInputChange(e, 'discount')}/> </label> </div>
                <div> <label> Minimum Units : <input className = "minUnits" type = "text" onChange = {(e) => this.handleInputChange(e, 'minUnits')}/> </label> </div>
                <div> <label> Maximum Units : <input className = "maxUnits" type = "text" onChange = {(e) => this.handleInputChange(e, 'maxUnits')}/> </label> </div>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Add Discount </button>
            </form>
        )
    }
}


export default ConditionalCategoryDiscountRule;