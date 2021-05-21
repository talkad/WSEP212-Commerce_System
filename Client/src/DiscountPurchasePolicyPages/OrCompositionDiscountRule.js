import React from 'react';
import Select from 'react-select';
import StaticUserInfo from "../MainPages/StaticUserInfo";


class OrCompositionDiscountRule extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'addDiscountRule',
            username: '1',
            storeId: '2',
            type: 'OrCompositionDiscountRule',
            category: '',
            discount: '',
            policyRules: '',
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
                <h1>Or Composition Discount Rule Page</h1>
                <div> <label> Username : <input readOnly value = {this.state.username} className = "username" type = "text" onChange = {(e) => this.handleInputChange(e, 'username')}/> </label> </div>
                <div> <label> Store Id : <input readOnly value = {this.state.storeId} className = "storeId" type = "text" onChange = {(e) => this.handleInputChange(e, 'storeId')}/> </label> </div>
                <div> <label> Category : <input className = "category" type = "text" onChange = {(e) => this.handleInputChange(e, 'category')}/> </label> </div>
                <div> <label> Discount : <input className = "discount" type = "text" onChange = {(e) => this.handleInputChange(e, 'discount')}/> </label> </div>
                <div> <label> Policy Rules : <input className = "policyRules" type = "text" onChange = {(e) => this.handleInputChange(e, 'policyRules')}/> </label> </div>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Add Discount </button>
            </form>
        )
    }
}


export default OrCompositionDiscountRule;