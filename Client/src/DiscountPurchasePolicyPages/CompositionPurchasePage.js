import React from 'react';
import Select from 'react-select';
import StaticUserInfo from "../API/StaticUserInfo";
import Connection from "../API/Connection";
import {Button} from 'react-bootstrap';

const PurchaseOptions = [
    { value: 'AND', label: 'AND' },
    { value: 'OR', label: 'OR' },
    { value: 'CONDITIONING', label: 'CONDITIONING' },
];

let listToSend = [];
let listToSend2 = [];

class CompositionPurchasePage extends React.Component {
    constructor(props) {
        super(props);
        this.handleOptionChange = this.handleOptionChange.bind(this);
        this.state = {
            selectedOption: '',
            functionName: 'addPurchaseRule',
            username: window.sessionStorage.getItem('username'),
            storeId: window.sessionStorage.getItem('storeID')(),
            type: '',
            PredicateCategoryCategory: '' , PredicateCategoryMinUnits: '', PredicateCategoryMaxUnits: '',
            PredicateBasketMinUnits: '', PredicateBasketMaxUnits: '', PredicateBasketMinPrice: '',
            PredicateProductProductId: '', PredicateProductMinUnits: '', PredicateProductMaxUnits: '',

            PredicateCategoryCategory2: '' , PredicateCategoryMinUnits2: '', PredicateCategoryMaxUnits2: '',
            PredicateBasketMinUnits2: '', PredicateBasketMaxUnits2: '', PredicateBasketMinPrice2: '',
            PredicateProductProductId2: '', PredicateProductMinUnits2: '', PredicateProductMaxUnits2: '',

        };
    }


    handleInputChange = (e, name) => {
        this.setState({
            [name]: e.target.value
        })
    }

    handleClick(e) {
        e.preventDefault();
        let policiesRule = listToSend;
        let policiesRule2 = listToSend2;

        this.state.selectedOption === 'AND' ? Connection.sendCompositionPurchaseAndOr(this.state.functionName, this.state.username, this.state.storeId, this.state.type, policiesRule).then(this.handleCompPoliciesResponse, Connection.handleReject) :
            this.state.selectedOption === 'OR' ? Connection.sendCompositionPurchaseAndOr(this.state.functionName, this.state.username, this.state.storeId, this.state.type, policiesRule).then(this.handleCompPoliciesResponse, Connection.handleReject) :
                            Connection.sendCompositionPurchaseConditioning(this.state.functionName, this.state.username, this.state.storeId, this.state.type, policiesRule, policiesRule2).then(this.handleCompPoliciesResponse, Connection.handleReject);
        listToSend = [];
        listToSend2 = [];
    }

    handleCompPoliciesResponse(result){
        if(!result.isFailure){
            alert("adding purchase success");
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

        this.state.selectedOption === 'AND' ? this.setState({type:'AndCompositionPurchaseRule'}) :
            this.state.selectedOption === 'OR' ? this.setState({type:'OrCompositionPurchaseRule'}) :
                this.setState({type:'ConditioningCompositionPurchaseRule'});
    };

    handleClickCondCategoryDis(e) {
        if (this.state.selectedOption === 'AND' || this.state.selectedOption === 'OR'){
            listToSend.push(JSON.stringify({type: "CategoryPurchaseRule", categoryPredicate: JSON.stringify({category: this.state.PredicateCategoryCategory, minUnits: this.state.PredicateCategoryMinUnits, maxUnits: this.state.PredicateCategoryMaxUnits}) }))
        }
        else if (this.state.selectedOption === 'CONDITIONING'){
            listToSend.push(JSON.stringify({categoryPredicate: JSON.stringify({category: this.state.PredicateCategoryCategory, minUnits: this.state.PredicateCategoryMinUnits, maxUnits: this.state.PredicateCategoryMaxUnits}) }))
        }
        alert('Purchase Category Rule Added')
    }

    handleClickCondBasketDis(e) {
        if (this.state.selectedOption === 'AND' || this.state.selectedOption === 'OR'){
            listToSend.push(JSON.stringify({type: "BasketPurchaseRule", basketPredicate: JSON.stringify({minUnits: this.state.PredicateBasketMinUnits, maxUnits: this.state.PredicateBasketMaxUnits, minPrice: this.state.PredicateBasketMinPrice}) }))
        }
        else if (this.state.selectedOption === 'CONDITIONING'){
            listToSend.push(JSON.stringify({basketPredicate: JSON.stringify({minUnits: this.state.PredicateBasketMinUnits, maxUnits: this.state.PredicateBasketMaxUnits, minPrice: this.state.PredicateBasketMinPrice}) }))
        }
        alert('Purchase Store Rule Added')
    }

    handleClickCondProductDis(e) {
        if (this.state.selectedOption === 'AND' || this.state.selectedOption === 'OR'){
            listToSend.push(JSON.stringify({type: "ProductPurchaseRule", productPredicate: JSON.stringify({productID: this.state.PredicateProductProductId, minUnits: this.state.PredicateProductMinUnits, maxUnits: this.state.PredicateProductMaxUnits}) }))
        }
        else if (this.state.selectedOption === 'CONDITIONING'){
            listToSend.push(JSON.stringify({productPredicate: JSON.stringify({productID: this.state.PredicateProductProductId, minUnits: this.state.PredicateProductMinUnits, maxUnits: this.state.PredicateProductMaxUnits}) }))
        }
        alert('Purchase Product Rule Added')
    }

    handleClickCondCategoryDis2(e) {
        listToSend2.push(JSON.stringify({categoryPredicate: JSON.stringify({category: this.state.PredicateCategoryCategory2, minUnits: this.state.PredicateCategoryMinUnits2, maxUnits: this.state.PredicateCategoryMaxUnits2}) }))
        alert('Purchase Category Rule Added')
    }

    handleClickCondBasketDis2(e) {
        listToSend2.push(JSON.stringify({basketPredicate: JSON.stringify({minUnits: this.state.PredicateBasketMinUnits2, maxUnits: this.state.PredicateBasketMaxUnits2, minPrice: this.state.PredicateBasketMinPrice2}) }))
        alert('Purchase Store Rule Added')
    }

    handleClickCondProductDis2(e) {
        listToSend2.push(JSON.stringify({productPredicate: JSON.stringify({productID: this.state.PredicateProductProductId2, minUnits: this.state.PredicateProductMinUnits2, maxUnits: this.state.PredicateProductMaxUnits2}) }))
        alert('Purchase Product Rule Added')
    }

    render() {
        //let show;
        let  body0, body1, body2, body3, body4, body5, body6, body7;

        if (this.state.selectedOption === 'AND' || this.state.selectedOption === 'OR'){
            //show = <div> <label> Category : <input className = "category" type = "text" onChange = {(e) => this.handleInputChange(e, 'category')}/> </label> <label> Discount : <input className = "discount" type = "text" onChange = {(e) => this.handleInputChange(e, 'discount')}/> </label> </div>

            body1 = <div> <label> Category Predicate ::: </label> <label> Category : <input className = "PredicateCategoryCategory" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateCategoryCategory')}/> </label>
                <label> Min Units : <input className = "PredicateCategoryMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateCategoryMinUnits')}/> </label> <label> Max Units : <input className = "PredicateCategoryMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateCategoryMaxUnits')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondCategoryDis(e)}> Add </Button> </div>

            body2 = <div> <label> Basket Predicate ::: </label> <label> Min Units : <input className = "PredicateBasketMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateBasketMinUnits')}/> </label>
                <label> Max Units : <input className = "PredicateBasketMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateBasketMaxUnits')}/> </label> <label> Min price : <input className = "PredicateBasketMinPrice" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateBasketMinPrice')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondBasketDis(e)} style={{marginLeft: "15px"}}> Add </Button> </div>

            body3 = <div> <label> Product Predicate ::: </label> <label> Product Id : <input className = "PredicateProductProductId" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateProductProductId')}/> </label>
                <label> Min Units : <input className = "PredicateProductMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateProductMinUnits')}/> </label> <label> Max Units : <input className = "PredicateProductMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateProductMaxUnits')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondProductDis(e)}> Add </Button> </div>
        }

        else if (this.state.selectedOption === 'CONDITIONING'){
            //show = <div> <label> Category : <input className = "category" type = "text" onChange = {(e) => this.handleInputChange(e, 'category')}/> </label> <label> Discount : <input className = "discount" type = "text" onChange = {(e) => this.handleInputChange(e, 'discount')}/> </label> </div>
            body0 = <div> <label> Predicates </label> </div>

            body1 = <div> <label> Category Predicate ::: </label> <label> Category : <input className = "PredicateCategoryCategory" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateCategoryCategory')}/> </label>
                <label> Min Units : <input className = "PredicateCategoryMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateCategoryMinUnits')}/> </label> <label> Max Units : <input className = "PredicateCategoryMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateCategoryMaxUnits')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondCategoryDis(e)}> Add </Button> </div>

            body2 = <div> <label> Store Predicate ::: </label>  <label> Min Units : <input className = "PredicateBasketMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateBasketMinUnits')}/> </label>
                <label> Max Units : <input className = "PredicateBasketMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateBasketMaxUnits')}/> </label> <label> Min price : <input className = "PredicateBasketMinPrice" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateBasketMinPrice')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondBasketDis(e)} style={{marginLeft: "25px"}}> Add </Button> </div>

            body3 = <div> <label> Product Predicate ::: </label> <label> Product Id : <input className = "PredicateProductProductId" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateProductProductId')}/> </label>
                <label> Min Units : <input className = "PredicateProductMinUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateProductMinUnits')}/> </label> <label> Max Units : <input className = "PredicateProductMaxUnits" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateProductMaxUnits')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondProductDis(e)}> Add </Button> </div>

            body4 = <div> <label> Implied Predicates </label> </div>

            body5 = <div> <label> Category Predicate ::: </label> <label> Category : <input className = "PredicateCategoryCategory2" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateCategoryCategory2')}/> </label>
                <label> Min Units : <input className = "PredicateCategoryMinUnits2" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateCategoryMinUnits2')}/> </label> <label> Max Units : <input className = "PredicateCategoryMaxUnits2" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateCategoryMaxUnits2')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondCategoryDis2(e)}> Add </Button> </div>

            body6 = <div> <label> Store Predicate ::: </label>  <label> Min Units : <input className = "PredicateBasketMinUnits2" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateBasketMinUnits2')}/> </label>
                <label> Max Units : <input className = "PredicateBasketMaxUnits2" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateBasketMaxUnits2')}/> </label> <label> Min price : <input className = "PredicateBasketMinPrice2" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateBasketMinPrice2')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondBasketDis2(e)} style={{marginLeft: "25px"}}> Add </Button> </div>

            body7 = <div> <label> Product Predicate ::: </label> <label> Product Id : <input className = "PredicateProductProductId2" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateProductProductId2')}/> </label>
                <label> Min Units : <input className = "PredicateProductMinUnits2" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateProductMinUnits2')}/> </label> <label> Max Units : <input className = "PredicateProductMaxUnits2" type = "text" style={{ width: "100px" }} onChange = {(e) => this.handleInputChange(e, 'PredicateProductMaxUnits2')}/> </label> <Button variant="dark" type = "button" onClick = {(e) => this.handleClickCondProductDis2(e)}> Add </Button>  </div>


        }

        //else show = '';

        return (
            <form>
                <h1>Purchase Page</h1>
                <Select onChange={this.handleOptionChange} options={PurchaseOptions}/>
                <div> <label> Username : <input readOnly value = {this.state.username} className = "username" type = "text" onChange = {(e) => this.handleInputChange(e, 'username')}/> </label> </div>
                <div> <label> Store Id : <input readOnly value = {this.state.storeId} className = "storeId" type = "text" onChange = {(e) => this.handleInputChange(e, 'storeId')}/> </label> </div>
                <div> <label> -- Purchase Rules -- </label> </div>
                {body0}
                {body1}
                {body2}
                {body3}
                {body4}
                {body5}
                {body6}
                {body7}
                <Button type = "button" onClick = {(e) => this.handleClick(e)}> Add Purchase Policy </Button>
            </form>
        )
    }
}

export default CompositionPurchasePage;