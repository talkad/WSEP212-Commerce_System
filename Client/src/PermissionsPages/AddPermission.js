import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";

/***
 * Use Case : 4.6.1
 */

class AddPermission extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'addPermission',
            permitting: StaticUserInfo.getUsername(),
            storeId: StaticUserInfo.getStoreId(),
            permitted: '',
            permissions: '',
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
                <h1>Add Permission Page </h1>
                <div> <label> Permitting : <input readOnly value = {this.state.permitting} className = "permitting" type = "text" onChange = {(e) => this.handleInputChange(e, 'permitting')}/> </label> </div>
                <div> <label> Store Id : <input readOnly value = {this.state.storeId} className = "storeId" type = "text" onChange = {(e) => this.handleInputChange(e, 'storeId')}/> </label> </div>
                <div> <label> Permitted : <input className = "permitted" type = "text" onChange = {(e) => this.handleInputChange(e, 'permitted')}/> </label> </div>
                <div> <label> Permissions* : <input className = "permissions" type = "text" placeholder='OPEN_STORE, REVIEW_PRODUCT,...' style={{width: "370px"}} onChange = {(e) => this.handleInputChange(e, 'keywords')}/> </label> </div>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Add Permission </button>
            </form>
        )
    }
}

export default AddPermission;