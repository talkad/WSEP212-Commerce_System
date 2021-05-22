import React from 'react';
import Select from 'react-select';

class AndCompositionDiscountRule extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            selectedOption: null,
            functionName: 'addDiscountRule',
            username: '1',
            storeId: '2',
            type: 'AndCompositionDiscountRule',
            category: '',
            discount: '',

            CategoryCategory: '', CategoryDiscount: '',
            StoreDiscount: '',
            ProductProductId: '', ProductDiscount: '',
            CondCategoryCategory: '', CondCategoryDiscount: '', CondCategoryMinUnits: '', CondCategoryMaxUnits: '',
            CondStoreDiscount: '', CondStoreMinUnits: '', CondStoreMaxUnits: '', CondStoreMinPrice: '',
            CondProductProductId: '', CondProductDiscount: '', CondProductMinUnits: '', CondProductMaxUnits: '',

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
                <h1>And Composition Discount Rule Page</h1>
                <div> <label> Username : <input readOnly value = {this.state.username} className = "username" type = "text" onChange = {(e) => this.handleInputChange(e, 'username')}/> </label> </div>
                <div> <label> Store Id : <input readOnly value = {this.state.storeId} className = "storeId" type = "text" onChange = {(e) => this.handleInputChange(e, 'storeId')}/> </label> </div>
                <div> <label> Category : <input className = "category" type = "text" onChange = {(e) => this.handleInputChange(e, 'category')}/> </label> </div>
                <div> <label> Discount : <input readOnly value = {'-100'} className = "discount" type = "text" onChange = {(e) => this.handleInputChange(e, 'discount')}/> </label> </div>
                <div> <label> -- Policy Rules -- </label> </div>
                <div> <label> Category Discount ::: </label> <label> Category : <input className = "CategoryCategory" type = "text" onChange = {(e) => this.handleInputChange(e, 'CategoryCategory')}/> </label> <label> Discount : <input className = "CategoryDiscount" type = "text" onChange = {(e) => this.handleInputChange(e, 'CategoryDiscount')}/> </label> </div>
                <div> <label> Store Discount ::: </label> <label> Category : <input className = "StoreDiscount" type = "text" onChange = {(e) => this.handleInputChange(e, 'StoreDiscount')}/> </label> </div>
                <div> <label> Product Discount ::: </label> <label> Product Id : <input className = "ProductProductId" type = "text" onChange = {(e) => this.handleInputChange(e, 'ProductProductId')}/> </label> <label> Discount : <input className = "ProductDiscount" type = "text" onChange = {(e) => this.handleInputChange(e, 'ProductDiscount')}/> </label> </div>
                <div> <label> Conditional Category Discount ::: </label> <label> Category : <input className = "CondCategoryCategory" type = "text" onChange = {(e) => this.handleInputChange(e, 'CondCategoryCategory')}/> </label> <label> Discount : <input className = "CondCategoryDiscount" type = "text" onChange = {(e) => this.handleInputChange(e, 'CondCategoryDiscount')}/> </label>
                    <label> Min Units : <input className = "CondCategoryMinUnits" type = "text" onChange = {(e) => this.handleInputChange(e, 'CondCategoryMinUnits')}/> </label> <label> Max Units : <input className = "CondCategoryMaxUnits" type = "text" onChange = {(e) => this.handleInputChange(e, 'CondCategoryMaxUnits')}/> </label></div>
                <div> <label> Conditional Store Discount ::: </label> <label> Discount : <input className = "CondStoreDiscount" type = "text" onChange = {(e) => this.handleInputChange(e, 'CondStoreDiscount')}/> </label> <label> Min Units : <input className = "CondStoreMinUnits" type = "text" onChange = {(e) => this.handleInputChange(e, 'CondStoreMinUnits')}/> </label>
                    <label> Max Units : <input className = "CondStoreMaxUnits" type = "text" onChange = {(e) => this.handleInputChange(e, 'CondStoreMaxUnits')}/> </label> <label> Min price : <input className = "CondStoreMinPrice" type = "text" onChange = {(e) => this.handleInputChange(e, 'CondStoreMinPrice')}/> </label> </div>
                <div> <label> Conditional Product Discount ::: </label> <label> Product Id : <input className = "CondProductProductId" type = "text" onChange = {(e) => this.handleInputChange(e, 'CondProductProductId')}/> </label> <label> Discount : <input className = "CondProductDiscount" type = "text" onChange = {(e) => this.handleInputChange(e, 'CondProductDiscount')}/> </label>
                    <label> Min Units : <input className = "CondProductMinUnits" type = "text" onChange = {(e) => this.handleInputChange(e, 'CondProductMinUnits')}/> </label> <label> Max Units : <input className = "CondProductMaxUnits" type = "text" onChange = {(e) => this.handleInputChange(e, 'CondProductMaxUnits')}/> </label> </div>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Add Policy </button>
            </form>
        )
    }
}


export default AndCompositionDiscountRule;