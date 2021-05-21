import React from 'react';
import Select from 'react-select';
import StaticUserInfo from "../API/StaticUserInfo";

const DiscountOptions = [
    { value: 'AND', label: 'AND' },
    { value: 'OR', label: 'OR' },
    { value: 'XOR', label: 'XOR' },
    { value: 'SUM', label: 'SUM' },
    { value: 'MAX', label: 'MAX' },
];

class CompositionPoliciesPage extends React.Component {
    constructor(props) {
        super(props);
        this.handleOptionChange = this.handleOptionChange.bind(this);
        this.state = {
            selectedOption: '',
            functionName: 'addDiscountRule',
            username: StaticUserInfo.getUsername(),
            storeId: StaticUserInfo.getStoreId(),
            type: 'AndCompositionDiscountRule',
            category: '',
            discount: '',
            xoresolve: '',

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

    handleOptionChange = selectedOption => {
        this.setState( {selectedOption : selectedOption.label});
        this.state.selectedOption === 'AND' ? this.setState({type:'AndCompositionDiscountRule'}) :
            this.state.selectedOption === 'OR' ? this.setState({type:'OrCompositionDiscountRule'}) :
                this.state.selectedOption === 'XOR' ? this.setState({type:'XorCompositionDiscountRule'}) :
                    this.state.selectedOption === 'SUM' ? this.setState({type:'SumCompositionDiscountRule'}) :
                        this.setState({type:'MaximumCompositionDiscountRule '});


    };


    render() {
        let show;
        if (this.state.selectedOption === 'AND' || this.state.selectedOption === 'OR')
            show = <div> <label> Category : <input className = "category" type = "text" onChange = {(e) => this.handleInputChange(e, 'category')}/> </label> <label> Discount : <input readOnly value = {'-100'} className = "discount" type = "text" onChange = {(e) => this.handleInputChange(e, 'discount')}/> </label> </div>
        else if (this.state.selectedOption === 'XOR')
            show = <div> <label> Category : <input className = "category" type = "text" onChange = {(e) => this.handleInputChange(e, 'category')}/> </label> <label> Discount : <input readOnly value = {'-100'} className = "discount" type = "text" onChange = {(e) => this.handleInputChange(e, 'discount')}/> </label>
                <label> XOR type : <input className = "xoresolve" type = "text" onChange = {(e) => this.handleInputChange(e, 'xoresolve')}/> </label> </div>
                else show = ''
        return (
            <form>
                <h1>Policy Page</h1>
                <Select value={this.state.selectedOption} onChange={this.handleOptionChange} options={DiscountOptions}/>
                <div> <label> Username : <input readOnly value = {this.state.username} className = "username" type = "text" onChange = {(e) => this.handleInputChange(e, 'username')}/> </label> </div>
                <div> <label> Store Id : <input readOnly value = {this.state.storeId} className = "storeId" type = "text" onChange = {(e) => this.handleInputChange(e, 'storeId')}/> </label> </div>
                {show}
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


export default CompositionPoliciesPage;