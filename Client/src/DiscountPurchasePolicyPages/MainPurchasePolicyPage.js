import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";
import ReactDOM from "react-dom";
import CategoryDiscountRule from "./CategoryDiscountRule";
import StoreDiscountRule from "./StoreDiscountRule";
import EditProduct from "../ProductPages/EditProduct";
import {Link} from "react-router-dom";
//import {Link} from "react";

class MainPurchasePolicyPage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: StaticUserInfo.getUsername(),
            storeId: StaticUserInfo.getStoreId(),
        };
    }

    handleClick(e, page) {
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
                <h1>Choose Purchase Policy Page </h1>
                <Link to = '/Category_Purchase'> <button type = "button"> Category Predicate </button> </Link>
                <Link to = '/Basket_Purchase'> <button type = "button"> Basket Predicate </button> </Link>
                <Link to = '/Product_Purchase'> <button type = "button"> Product Predicate </button> </Link>
                <div><Link to = '/Composition_Purchase_Page'> <button type = "button"> Add Composition Purchase </button> </Link></div>
            </form>
        )
    }
}

export default MainPurchasePolicyPage;