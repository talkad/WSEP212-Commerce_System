import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";

/***
 * Use Case : 4.1.2
 */

class DeleteProduct extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'removeProductsFromStore',
            username: StaticUserInfo.getUsername(),
            storeId: StaticUserInfo.getStoreId(),
            productId: '',
            amount: '',
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
                <h1>Delete Product Page </h1>
                <div> <label> Product Id : <input className = "productId" type = "text" onChange = {(e) => this.handleInputChange(e, 'productId')}/> </label> </div>
                <div> <label> Amount : <input className = "amount" type = "text" onChange = {(e) => this.handleInputChange(e, 'amount')}/> </label> </div>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Delete Product </button>
            </form>
        )
    }
}

export default DeleteProduct;