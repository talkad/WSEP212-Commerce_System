import React from 'react';
import Select from 'react-select';
import StaticUserInfo from "../API/StaticUserInfo";


class StoreDiscountRule extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'addDiscountRule',
            username: StaticUserInfo.getUsername(),
            storeId: StaticUserInfo.getStoreId(),
            type: 'StoreDiscountRule',
            discount: '',
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
                <h1>Add Store Discount Rule Page</h1>
                <div> <label> Username : <input readOnly value = {this.state.username} className = "username" type = "text" onChange = {(e) => this.handleInputChange(e, 'username')}/> </label> </div>
                <div> <label> Store Id : <input readOnly value = {this.state.storeId} className = "storeId" type = "text" onChange = {(e) => this.handleInputChange(e, 'storeId')}/> </label> </div>
                <div> <label> Discount : <input className = "discount" type = "text" onChange = {(e) => this.handleInputChange(e, 'discount')}/> </label> </div>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Add Discount </button>
            </form>
        )
    }
}


export default StoreDiscountRule;