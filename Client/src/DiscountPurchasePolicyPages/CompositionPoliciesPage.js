import React from 'react';
import Select from 'react-select';
import StaticUserInfo from "../API/StaticUserInfo";
import Connection from "../API/Connection";

const DiscountOptions = [
    { value: 'AND', label: 'AND' },
    { value: 'OR', label: 'OR' },
    { value: 'XOR', label: 'XOR' },
    { value: 'SUM', label: 'SUM' },
    { value: 'MAX', label: 'MAX' },
];
let Category = [];
let Store = [];
let Product = [];
let CondCategory = [];
let CondStore = [];
let CondProduct = [];

class CompositionPoliciesPage extends React.Component {
    constructor(props) {
        super(props);
        this.handleOptionChange = this.handleOptionChange.bind(this);
        this.state = {
            selectedOption: '',
            functionName: 'addDiscountRule',
            username: StaticUserInfo.getUsername(),
            storeId: StaticUserInfo.getStoreId(),
            type: '',
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
        /*
        let Category = JSON.stringify({category: this.state.CategoryCategory, discount: this.state.CategoryDiscount});
        let Store = JSON.stringify({discount: this.state.StoreDiscount});
        let Product = JSON.stringify({productID: this.state.ProductProductId})
        let CondCategory = JSON.stringify({category: this.state.CondCategoryCategory, discount: this.state.CondCategoryDiscount, minUnits: this.state.CondCategoryMinUnits, maxUnits: this.state.CondCategoryMaxUnits})
        let CondStore = JSON.stringify({discount: this.state.CondStoreDiscount, minUnits: this.state.CondStoreMinUnits, maxUnits: this.state.CondStoreMaxUnits, minPrice: this.state.CondStoreMinPrice})
        let CondProduct = JSON.stringify({productId: this.state.CondProductProductId, discount: this.state.CondProductDiscount, minUnits: this.state.CondProductMinUnits, maxUnits: this.state.CondProductMaxUnits})
        let policiesRule = JSON.stringify([Category + ',' + Store + ',' + Product + ',' + CondCategory + ',' + CondStore + ',' + CondProduct])
         */

        let policiesRule = JSON.stringify([Category + ',' + Store + ',' + Product + ',' + CondCategory + ',' + CondStore + ',' + CondProduct]);
        this.state.selectedOption === 'AND' ? Connection.sendCompositionPoliciesAndOr(this.state.functionName, this.state.username, this.state.storeId, this.state.type, this.state.category, this.state.discount, policiesRule).then(this.handleCompPoliciesResponse, Connection.handleReject) :
            this.state.selectedOption === 'OR' ? Connection.sendCompositionPoliciesAndOr(this.state.functionName, this.state.username, this.state.storeId, this.state.type, this.state.category, this.state.discount, policiesRule).then(this.handleCompPoliciesResponse, Connection.handleReject) :
                this.state.selectedOption === 'XOR' ? Connection.sendCompositionPoliciesSumMax(this.state.functionName, this.state.username, this.state.storeId, this.state.type, this.state.discount, policiesRule, this.state.xoresolve).then(this.handleCompPoliciesResponse, Connection.handleReject) :
                    this.state.selectedOption === 'SUM' ? Connection.sendCompositionPoliciesSumMax(this.state.functionName, this.state.username, this.state.storeId, this.state.type, policiesRule).then(this.handleCompPoliciesResponse, Connection.handleReject) :
                        Connection.sendCompositionPoliciesSumMax(this.state.functionName, this.state.username, this.state.storeId, this.state.type, policiesRule).then(this.handleCompPoliciesResponse, Connection.handleReject);
    }

    handleCompPoliciesResponse(result){
        if(!result.isFailure){
            alert("adding policy success");
        }
        else{
            alert(result.errMsg);
        }
    }

    handleOptionChange = selectedOption => {
        this.setState( {selectedOption : selectedOption.label});
        this.state.selectedOption === 'AND' ? this.setState({type:'AndCompositionDiscountRule'}) :
            this.state.selectedOption === 'OR' ? this.setState({type:'OrCompositionDiscountRule'}) :
                this.state.selectedOption === 'XOR' ? this.setState({type:'XorCompositionDiscountRule'}) :
                    this.state.selectedOption === 'SUM' ? this.setState({type:'SumCompositionDiscountRule'}) :
                        this.setState({type:'MaximumCompositionDiscountRule '});
    };

    handleClickCategoryDis(e) {
        Category.push(JSON.stringify({category: this.state.CategoryCategory, discount: this.state.CategoryDiscount}));
        //this.setState( {CategoryCategory : ''});
        //this.setState( {CategoryDiscount : ''});
        alert('Category Rule Added')
    }

    handleClickStoreDis(e) {
        Store.push(JSON.stringify({discount: this.state.StoreDiscount}));
        alert('Store Rule Added')
    }

    handleClickProductDis(e) {
        Store.push(JSON.stringify({productID: this.state.ProductProductId}));
        alert('Product Rule Added')
    }

    handleClickCondCategoryDis(e) {
        CondCategory.push(JSON.stringify({category: this.state.CondCategoryCategory, discount: this.state.CondCategoryDiscount, minUnits: this.state.CondCategoryMinUnits, maxUnits: this.state.CondCategoryMaxUnits}));
        alert('Conditional Category Rule Added')
    }

    handleClickCondStoreDis(e) {
        CondStore.push(JSON.stringify({discount: this.state.CondStoreDiscount, minUnits: this.state.CondStoreMinUnits, maxUnits: this.state.CondStoreMaxUnits, minPrice: this.state.CondStoreMinPrice}));
        alert('Conditional Store Rule Added')
    }

    handleClickCondProductDis(e) {
        CondProduct.push(JSON.stringify({productId: this.state.CondProductProductId, discount: this.state.CondProductDiscount, minUnits: this.state.CondProductMinUnits, maxUnits: this.state.CondProductMaxUnits}));
        alert('Conditional Product Rule Added')
    }

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
                <div> <label> Category Discount ::: </label> <label> Category : <input className = "CategoryCategory" type = "text"  style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CategoryCategory')}/> </label> <label> Discount : <input className = "CategoryDiscount" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CategoryDiscount')}/> </label> <button type = "button" onClick = {(e) => this.handleClickCategoryDis(e)}> Add </button> ----------------------------------------------------------------------- </div>
                <div> <label> Store Discount ::: </label> <label> Category : <input className = "StoreDiscount" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'StoreDiscount')}/> </label> <button type = "button" onClick = {(e) => this.handleClickStoreDis(e)}> Add </button> ------------------------------------------------------------------------------------------------------- </div>
                <div> <label> Product Discount ::: </label> <label> Product Id : <input className = "ProductProductId" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'ProductProductId')}/> </label> <label> Discount : <input className = "ProductDiscount" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'ProductDiscount')}/> </label> <button type = "button" onClick = {(e) => this.handleClickProductDis(e)}> Add </button> ----------------------------------------------------------------------- </div>
                <div> <label> Conditional Category Discount ::: </label> <label> Category : <input className = "CondCategoryCategory" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondCategoryCategory')}/> </label> <label> Discount : <input className = "CondCategoryDiscount" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondCategoryDiscount')}/> </label>
                    <label> Min Units : <input className = "CondCategoryMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondCategoryMinUnits')}/> </label> <label> Max Units : <input className = "CondCategoryMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondCategoryMaxUnits')}/> </label> <button type = "button" onClick = {(e) => this.handleClickCondCategoryDis(e)}> Add </button> - </div>
                <div> <label> Conditional Store Discount ::: </label> <label> Discount : <input className = "CondStoreDiscount" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondStoreDiscount')}/> </label> <label> Min Units : <input className = "CondStoreMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondStoreMinUnits')}/> </label>
                    <label> Max Units : <input className = "CondStoreMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondStoreMaxUnits')}/> </label> <label> Min price : <input className = "CondStoreMinPrice" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondStoreMinPrice')}/> </label> <button type = "button" onClick = {(e) => this.handleClickCondStoreDis(e)}> Add </button> ----- </div>
                <div> <label> Conditional Product Discount ::: </label> <label> Product Id : <input className = "CondProductProductId" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondProductProductId')}/> </label> <label> Discount : <input className = "CondProductDiscount" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondProductDiscount')}/> </label>
                    <label> Min Units : <input className = "CondProductMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondProductMinUnits')}/> </label> <label> Max Units : <input className = "CondProductMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondProductMaxUnits')}/> </label> <button type = "button" onClick = {(e) => this.handleClickCondProductDis(e)}> Add </button> - </div>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Add Policy </button>
            </form>
        )
    }
}


export default CompositionPoliciesPage;