import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";
import ReactDOM from "react-dom";
import CategoryDiscountRule from "./CategoryDiscountRule";
import StoreDiscountRule from "./StoreDiscountRule";

class MainDiscountPolicyPage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: StaticUserInfo.getUsername(),
            storeId: StaticUserInfo.getStoreId(),
        };
    }

    handleClick(e) {
        // access input values in the state
        console.log(this.state)
        e.preventDefault();
    }

    handleInputChange = (e, name) => {
        this.setState({
            [name]: e.target.value
        })
    }

    render() {
        return (
            <form>
                <h1>Choose Discount Policy Page </h1>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Category Discount </button>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Store Discount </button>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Product Discount </button>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Conditional Category Discount </button>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Conditional Store Discount </button>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Conditional Product Discount </button>
                <div><button type = "button" onClick = {(e) => this.handleClick(e)}> Add Policy </button> </div>
            </form>
        )
    }
}

export default MainDiscountPolicyPage;