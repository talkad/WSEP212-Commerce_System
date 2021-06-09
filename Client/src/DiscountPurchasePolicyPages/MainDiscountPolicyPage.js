    import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";
import ReactDOM from "react-dom";
import CategoryDiscountRule from "./CategoryDiscountRule";
import StoreDiscountRule from "./StoreDiscountRule";
import EditProduct from "../ProductPages/EditProduct";
import {Link} from "react-router-dom";
//import {Link} from "react";

class MainDiscountPolicyPage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: StaticUserInfo.getUsername(),
            storeId: StaticUserInfo.getStoreId(),
        };
    }

    handleClick(e, page) {
        //console.log(this.state)
        e.preventDefault();
        //page === 'Category Discount' ? ReactDOM.render(<React.StrictMode><CategoryDiscountRule /></React.StrictMode>, document.getElementById('root')) : null
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
                <Link to = '/Category_Discount_Rule'> <button type = "button"> Category Discount </button> </Link>
                <Link to = '/Store_Discount_Rule'> <button type = "button"> Store Discount </button> </Link>
                <Link to = '/Product_Discount_Rule'> <button type = "button"> Product Discount </button> </Link>
                <Link to = '/Conditional_Category_Discount_Rule'> <button type = "button"> Cond Category Rule </button> </Link>
                <Link to = '/Conditional_Store_Discount_Rule'> <button type = "button"> Cond Store Rule </button> </Link>
                <Link to = '/Conditional_Product_Discount_Rule'> <button type = "button"> Cond Product Rule </button> </Link>
                <div><Link to = '/Composition_Policies_Page'> <button type = "button"> Add Policy Rule </button> </Link></div>
            </form>
        )
    }
}

export default MainDiscountPolicyPage;