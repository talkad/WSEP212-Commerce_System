import React from 'react';
import Select from 'react-select';
import StaticUserInfo from "../API/StaticUserInfo";


class ConditionalStoreDiscountRule extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'addDiscountRule',
            username: StaticUserInfo.getUsername(),
            storeId: StaticUserInfo.getStoreId(),
            type: 'ConditionalStoreDiscountRule',
            discount: '',
            minUnits: '',
            maxUnits: '',
            minPrice: '',
        };
    }


    handleInputChange = (e, name) => {
        this.setState({
            [name]: e.target.value
        })
    }

    handleClick(e) {
        e.preventDefault();
    }

    render() {
        return (
            <form>
                <h1>Add Conditional Store Discount Rule Page</h1>
                <div> <label> Username : <input readOnly value = {this.state.username} className = "username" type = "text" onChange = {(e) => this.handleInputChange(e, 'username')}/> </label> </div>
                <div> <label> Store Id : <input readOnly value = {this.state.storeId} className = "storeId" type = "text" onChange = {(e) => this.handleInputChange(e, 'storeId')}/> </label> </div>
                <div> <label> Discount : <input className = "discount" type = "text" onChange = {(e) => this.handleInputChange(e, 'discount')}/> </label> </div>
                <div> <label> Minimum Units : <input className = "minUnits" type = "text" onChange = {(e) => this.handleInputChange(e, 'minUnits')}/> </label> </div>
                <div> <label> Maximum Units : <input className = "maxUnits" type = "text" onChange = {(e) => this.handleInputChange(e, 'maxUnits')}/> </label> </div>
                <div> <label> Minimum Price : <input className = "minPrice" type = "text" onChange = {(e) => this.handleInputChange(e, 'minPrice')}/> </label> </div>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Add Discount </button>
            </form>
        )
    }
}


export default ConditionalStoreDiscountRule;