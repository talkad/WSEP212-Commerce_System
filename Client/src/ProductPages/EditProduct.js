import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";
import Connection from "../API/Connection";

/***
 * Use Case : 4.1.3
 */

class EditProduct extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'updateProductInfo',
            username: window.sessionStorage.getItem('username'),
            storeId: window.sessionStorage.getItem('storeID')(),
            productId: '',
            newPrice: '',
            newName: '',
        };
    }

    handleClick(e) {
        e.preventDefault();
        Connection.sendEditProduct(this.state.functionName,this.state.username,this.state.storeId,this.state.productId, this.state.newPrice, this.state.newName).then(this.handleEditProductResponse, Connection.handleReject)
    }

    handleEditProductResponse(result){
        if(!result.isFailure){
            alert("edit product success");
        }
        else{
            alert(result.errMsg);
        }
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