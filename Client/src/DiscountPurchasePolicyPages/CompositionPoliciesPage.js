import React from 'react';
import {Button} from 'react-bootstrap';
import Select from 'react-select';
import StaticUserInfo from "../API/StaticUserInfo";
import Connection from "../API/Connection";
import {Option} from "react-bootstrap-icons";

const DiscountOptions = [
    { value: 'AND', label: 'AND' },
    { value: 'OR', label: 'OR' },
    { value: 'XOR', label: 'XOR' },
    { value: 'SUM', label: 'SUM' },
    { value: 'MAX', label: 'MAX' },
    { value: 'TERM', label: 'TERM' },
];

let listToSend = [];

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

        //let policiesRule = JSON.stringify(listToSend);
        let policiesRule = listToSend;
        //let policiesRule = JSON.stringify([CondCategory + ',' + CondStore + ',' + CondProduct]);
        this.state.selectedOption === 'AND' ? Connection.sendCompositionPoliciesAndOr(this.state.functionName, this.state.username, this.state.storeId, this.state.type, this.state.category, this.state.discount, policiesRule).then(this.handleCompPoliciesResponse, Connection.handleReject) :
            this.state.selectedOption === 'OR' ? Connection.sendCompositionPoliciesAndOr(this.state.functionName, this.state.username, this.state.storeId, this.state.type, this.state.category, this.state.discount, policiesRule).then(this.handleCompPoliciesResponse, Connection.handleReject) :
                this.state.selectedOption === 'XOR' ? Connection.sendCompositionPoliciesXor(this.state.functionName, this.state.username, this.state.storeId, this.state.type, this.state.discount, policiesRule, this.state.xoresolve).then(this.handleCompPoliciesResponse, Connection.handleReject) :
                    this.state.selectedOption === 'SUM' ? Connection.sendCompositionPoliciesSumMax(this.state.functionName, this.state.username, this.state.storeId, this.state.type, policiesRule).then(this.handleCompPoliciesResponse, Connection.handleReject) :
                        this.state.selectedOption === 'MAX' ? Connection.sendCompositionPoliciesSumMax(this.state.functionName, this.state.username, this.state.storeId, this.state.type, policiesRule).then(this.handleCompPoliciesResponse, Connection.handleReject):
                            Connection.sendCompositionPoliciesTerm(this.state.functionName, this.state.username, this.state.storeId, this.state.type, this.state.category, this.state.discount, policiesRule).then(this.handleCompPoliciesResponse, Connection.handleReject);
        listToSend = [];
    }

    handleCompPoliciesResponse(result){
        if(!result.isFailure){
            alert("adding policy success");
        }
        else{
            if(listToSend.length === 0)
                alert("Please fill composed rule with at least one simple rule.");
            else
                alert(result.errMsg);
        }
    }

    handleOptionChange = selectedOption => {
        // this.setState( {selectedOption : selectedOption.label});
        this.state.selectedOption = selectedOption.label;

        this.state.selectedOption === 'AND' ? this.setState({type:'AndCompositionDiscountRule'}) :
            this.state.selectedOption === 'OR' ? this.setState({type:'OrCompositionDiscountRule'}) :
                this.state.selectedOption === 'XOR' ? this.setState({type:'XorCompositionDiscountRule'}) :
                    this.state.selectedOption === 'SUM' ? this.setState({type:'SumCompositionDiscountRule'}) :
                        this.state.selectedOption === 'MAX' ? this.setState({type:'MaximumCompositionDiscountRule'}):
                            this.setState({type:'TermsCompositionDiscountRule'});
    };

    // handleClickCategoryDis(e) {
    //     Category.push(JSON.stringify({category: this.state.CategoryCategory, discount: this.state.CategoryDiscount}));
    //     //this.setState( {CategoryCategory : ''});
    //     //this.setState( {CategoryDiscount : ''});
    //     alert('Category Rule Added')
    // }
    //
    // handleClickStoreDis(e) {
    //     Store.push(JSON.stringify({discount: this.state.StoreDiscount}));
    //     alert('Store Rule Added')
    // }
    //
    // handleClickProductDis(e) {
    //     Store.push(JSON.stringify({productID: this.state.ProductProductId}));
    //     alert('Product Rule Added')
    // }

    handleClickCondCategoryDis(e) {
        if (this.state.selectedOption === 'SUM' || this.state.selectedOption === 'MAX'){
            listToSend.push(JSON.stringify({type: "ConditionalCategoryDiscountRule", category: this.state.category, discount: "-100", categoryPredicate: JSON.stringify({category: this.state.CondCategoryCategory, discount: this.state.CondCategoryDiscount, minUnits: this.state.CondCategoryMinUnits, maxUnits: this.state.CondCategoryMaxUnits}) }))
        }

        else if (this.state.selectedOption === 'TERM'){
            listToSend.push(JSON.stringify({categoryPredicate: JSON.stringify({category: this.state.CondCategoryCategory, minUnits: this.state.CondCategoryMinUnits, maxUnits: this.state.CondCategoryMaxUnits}) }))
        }

        else listToSend.push(JSON.stringify({type: "ConditionalCategoryDiscountRule", category: this.state.category, discount: "-100", categoryPredicate: JSON.stringify({category: this.state.CondCategoryCategory, discount: "-100", minUnits: this.state.CondCategoryMinUnits, maxUnits: this.state.CondCategoryMaxUnits}) }))
        alert('Conditional Category Rule Added')
    }

    handleClickCondStoreDis(e) {
        if (this.state.selectedOption === 'SUM' || this.state.selectedOption === 'MAX'){
            listToSend.push(JSON.stringify({type: "ConditionalStoreDiscountRule", discount: this.state.CondStoreDiscount, storePredicate: JSON.stringify({minUnits: this.state.CondStoreMinUnits, maxUnits: this.state.CondStoreMaxUnits, minPrice: this.state.CondStoreMinPrice}) }))
        }

        else if (this.state.selectedOption === 'TERM'){
            listToSend.push(JSON.stringify({storePredicate: JSON.stringify({minUnits: this.state.CondStoreMinUnits, maxUnits: this.state.CondStoreMaxUnits, minPrice: this.state.CondStoreMinPrice}) }))
        }

        else listToSend.push(JSON.stringify({type: "ConditionalStoreDiscountRule", discount: "-100", storePredicate: JSON.stringify({minUnits: this.state.CondStoreMinUnits, maxUnits: this.state.CondStoreMaxUnits, minPrice: this.state.CondStoreMinPrice}) }))
        alert('Conditional Store Rule Added')
    }

    handleClickCondProductDis(e) {
        if (this.state.selectedOption === 'SUM' || this.state.selectedOption === 'MAX'){
            listToSend.push(JSON.stringify({type: "ConditionalProductDiscountRule", productID: this.state.CondProductProductId, discount: this.state.CondProductDiscount, productPredicate: JSON.stringify({productID: this.state.CondProductProductId, minUnits: this.state.CondProductMinUnits, maxUnits: this.state.CondProductMaxUnits}) }))
        }

        else if (this.state.selectedOption === 'TERM'){
            listToSend.push(JSON.stringify({productPredicate: JSON.stringify({productID: this.state.CondProductProductId, minUnits: this.state.CondProductMinUnits, maxUnits: this.state.CondProductMaxUnits}) }))
        }

        else listToSend.push(JSON.stringify({type: "ConditionalProductDiscountRule", productID: this.state.CondProductProductId, discount: "-100", productPredicate: JSON.stringify({productID: this.state.CondProductProductId, minUnits: this.state.CondProductMinUnits, maxUnits: this.state.CondProductMaxUnits}) }))
        alert('Conditional Product Rule Added')
    }

    render() {
        let show;
        let body1, body2, body3;
        let button_margin = {marginLeft: "25px" };
        if (this.state.selectedOption === 'AND' || this.state.selectedOption === 'OR'){
            show = <div> <label> Category : <input className = "category" type = "text" onChange = {(e) => this.handleInputChange(e, 'category')}/> </label> <label> Discount : <input className = "discount" type = "text" onChange = {(e) => this.handleInputChange(e, 'discount')}/> </label> </div>

            body1 = <div> <label> Conditional Category Discount ::: </label> <label> Category : <input className = "CondCategoryCategory" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondCategoryCategory')}/> </label> <label> Discount : <input className = "CondCategoryDiscount" type = "text" style={{ width: "100px" }} readOnly value={'-100'} onChange = {(e) => this.handleInputChange(e, 'CondCategoryDiscount')}/> </label>
                <label> Min Units : <input className = "CondCategoryMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondCategoryMinUnits')}/> </label> <label> Max Units : <input className = "CondCategoryMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondCategoryMaxUnits')}/> </label> <Button variant="dark"   type = "button" onClick = {(e) => this.handleClickCondCategoryDis(e) }> Add </Button>  </div>

            body2 = <div> <label> Conditional Store Discount ::: </label> <label> Discount : <input className = "CondStoreDiscount" type = "text" style={{ width: "100px" }} readOnly value={'-100'} onChange = {(e) => this.handleInputChange(e, 'CondStoreDiscount')}/> </label> <label> Min Units : <input className = "CondStoreMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondStoreMinUnits')}/> </label>
                <label> Max Units : <input className = "CondStoreMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondStoreMaxUnits')}/> </label> <label> Min price : <input className = "CondStoreMinPrice" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondStoreMinPrice')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondStoreDis(e)} style={button_margin}> Add </Button>  </div>

            body3 = <div> <label> Conditional Product Discount ::: </label> <label> Product Id : <input className = "CondProductProductId" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondProductProductId')}/> </label> <label> Discount : <input className = "CondProductDiscount" type = "text" style={{ width: "100px" }} readOnly value={'-100'} onChange = {(e) => this.handleInputChange(e, 'CondProductDiscount')}/> </label>
                <label> Min Units : <input className = "CondProductMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondProductMinUnits')}/> </label> <label> Max Units : <input className = "CondProductMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondProductMaxUnits')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondProductDis(e)}> Add </Button>  </div>
        }

        else if (this.state.selectedOption === 'XOR'){
            show = <div> <label> Category : <input className = "category" type = "text" onChange = {(e) => this.handleInputChange(e, 'category')}/> </label> <label> Discount : <input className = "discount" type = "text" onChange = {(e) => this.handleInputChange(e, 'discount')}/> </label>
                <label> XOR type : <input className = "xoresolve" type = "text" onChange = {(e) => this.handleInputChange(e, 'xoresolve')}/> </label> </div>

            body1 = <div> <label> Conditional Category Discount ::: </label> <label> Category : <input className = "CondCategoryCategory" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondCategoryCategory')}/> </label> <label> Discount : <input className = "CondCategoryDiscount" type = "text" style={{ width: "100px" }} readOnly value={'-100'} onChange = {(e) => this.handleInputChange(e, 'CondCategoryDiscount')}/> </label>
                <label> Min Units : <input className = "CondCategoryMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondCategoryMinUnits')}/> </label> <label> Max Units : <input className = "CondCategoryMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondCategoryMaxUnits')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondCategoryDis(e)}> Add </Button>  </div>

            body2 = <div> <label> Conditional Store Discount ::: </label> <label> Discount : <input className = "CondStoreDiscount" type = "text" style={{ width: "100px" }} readOnly value={'-100'} onChange = {(e) => this.handleInputChange(e, 'CondStoreDiscount')}/> </label> <label> Min Units : <input className = "CondStoreMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondStoreMinUnits')}/> </label>
                <label> Max Units : <input className = "CondStoreMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondStoreMaxUnits')}/> </label> <label> Min price : <input className = "CondStoreMinPrice" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondStoreMinPrice')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondStoreDis(e)} style={button_margin}> Add </Button>  </div>

            body3 = <div> <label> Conditional Product Discount ::: </label> <label> Product Id : <input className = "CondProductProductId" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondProductProductId')}/> </label> <label> Discount : <input className = "CondProductDiscount" type = "text" style={{ width: "100px" }} readOnly value={'-100'} onChange = {(e) => this.handleInputChange(e, 'CondProductDiscount')}/> </label>
                <label> Min Units : <input className = "CondProductMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondProductMinUnits')}/> </label> <label> Max Units : <input className = "CondProductMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondProductMaxUnits')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondProductDis(e)}> Add </Button>  </div>
        }

        else if (this.state.selectedOption === 'SUM' || this.state.selectedOption === 'MAX'){
            show = ''

            body1 = <div> <label> Conditional Category Discount ::: </label> <label> Category : <input className = "CondCategoryCategory" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondCategoryCategory')}/> </label> <label> Discount : <input className = "CondCategoryDiscount" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondCategoryDiscount')}/> </label>
                <label> Min Units : <input className = "CondCategoryMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondCategoryMinUnits')}/> </label> <label> Max Units : <input className = "CondCategoryMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondCategoryMaxUnits')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondCategoryDis(e)}> Add </Button>  </div>

            body2 = <div> <label> Conditional Store Discount ::: </label> <label> Discount : <input className = "CondStoreDiscount" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondStoreDiscount')}/> </label> <label> Min Units : <input className = "CondStoreMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondStoreMinUnits')}/> </label>
                <label> Max Units : <input className = "CondStoreMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondStoreMaxUnits')}/> </label> <label> Min price : <input className = "CondStoreMinPrice" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondStoreMinPrice')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondStoreDis(e)} style={button_margin}> Add </Button>  </div>

            body3 = <div> <label> Conditional Product Discount ::: </label> <label> Product Id : <input className = "CondProductProductId" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondProductProductId')}/> </label> <label> Discount : <input className = "CondProductDiscount" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondProductDiscount')}/> </label>
                <label> Min Units : <input className = "CondProductMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondProductMinUnits')}/> </label> <label> Max Units : <input className = "CondProductMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondProductMaxUnits')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondProductDis(e)}> Add </Button>  </div>
        }

        else if (this.state.selectedOption === 'TERM'){
            show = <div> <label> Category : <input className = "category" type = "text" onChange = {(e) => this.handleInputChange(e, 'category')}/> </label> <label> Discount : <input className = "discount" type = "text" onChange = {(e) => this.handleInputChange(e, 'discount')}/> </label> </div>

            body1 = <div> <label> Category Predicate ::: </label> <label> Category : <input className = "CondCategoryCategory" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondCategoryCategory')}/> </label>
                <label> Min Units : <input className = "CondCategoryMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondCategoryMinUnits')}/> </label> <label> Max Units : <input className = "CondCategoryMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondCategoryMaxUnits')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondCategoryDis(e)}> Add </Button>  </div>

            body2 = <div> <label> Store Predicate ::: </label>  <label> Min Units : <input className = "CondStoreMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondStoreMinUnits')}/> </label>
                <label> Max Units : <input className = "CondStoreMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondStoreMaxUnits')}/> </label> <label> Min price : <input className = "CondStoreMinPrice" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondStoreMinPrice')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondStoreDis(e)} style={button_margin}> Add </Button> </div>

            body3 = <div> <label> Product Predicate ::: </label> <label> Product Id : <input className = "CondProductProductId" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondProductProductId')}/> </label>
                <label> Min Units : <input className = "CondProductMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondProductMinUnits')}/> </label> <label> Max Units : <input className = "CondProductMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'CondProductMaxUnits')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondProductDis(e)}> Add </Button>  </div>

        }

        else show = '';

        return (
            <form>
                <h1>Policy Page</h1>
                <Select onChange={this.handleOptionChange} options={DiscountOptions} isSearchable={false} />

                <div> <label> Username : <input readOnly value = {this.state.username} className = "username" type = "text" onChange = {(e) => this.handleInputChange(e, 'username')}/> </label> </div>
                <div> <label> Store Id : <input readOnly value = {this.state.storeId} className = "storeId" type = "text" onChange = {(e) => this.handleInputChange(e, 'storeId')}/> </label> </div>
                {show}
                <div> <label> -- Policy Rules -- </label> </div>
                {body1}
                {body2}
                {body3}
                <Button type = "button" onClick = {(e) => this.handleClick(e)}> Add Policy </Button>
            </form>
        )
    }
}





export default CompositionPoliciesPage;