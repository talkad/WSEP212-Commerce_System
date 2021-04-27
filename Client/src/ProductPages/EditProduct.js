import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";

/***
 * Use Case : 4.1.3
 */

class EditProduct extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'updateProductInfo',
            username: StaticUserInfo.getUsername(),
            storeId: StaticUserInfo.getStoreId(),
            productId: '',
            newPrice: '',
            newName: '',
        };
    }

    handleClick(e) {
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
                <h1>Edit Product Page </h1>
                <div> <label> Product Id : <input className = "productId" type = "text" onChange = {(e) => this.handleInputChange(e, 'productId')}/> </label> </div>
                <div> <label> New Price : <input className = "newPrice" type = "text" onChange = {(e) => this.handleInputChange(e, 'newPrice')}/> </label> </div>
                <div> <label> New Name : <input className = "newName" type = "text" onChange = {(e) => this.handleInputChange(e, 'newName')}/> </label> </div>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Edit Product </button>
            </form>
        )
    }
}

export default EditProduct;