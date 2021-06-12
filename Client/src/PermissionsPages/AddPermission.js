import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";
import Connection from "../API/Connection";
import {Form} from 'react-bootstrap'


class AddPermission extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'addPermission',
            permitting: window.sessionStorage.getItem('username'),
            storeId: window.sessionStorage.getItem('storeID'),
            permitted: '',
            permissions: '',
        };
    }

    handleClick(e) {
        console.log(this.state.permissions)
        Connection.sendPermission(this.state.functionName, this.state.permitting, this.state.storeId, this.state.permitted, this.state.permissions).then(this.handleResponse, Connection.handleReject);
        e.preventDefault();
    }

    handleInputChange = (e, name) => {
        this.setState({
            [name]: e.target.value
        })
    }

    handleResponse(result) {
        alert(result.errMsg);
    }

    render() {
        return (
            <form>
                <h1>Add Permission Page </h1>
                <div> <label> Permitting : <input readOnly value = {this.state.permitting} className = "permitting" type = "text" onChange = {(e) => this.handleInputChange(e, 'permitting')}/> </label> </div>
                <div> <label> Store Id : <input readOnly value = {this.state.storeId} className = "storeId" type = "text" onChange = {(e) => this.handleInputChange(e, 'storeId')}/> </label> </div>
                <div> <label> Permitted : <input className = "permitted" type = "text" onChange = {(e) => this.handleInputChange(e, 'permitted')}/> </label> </div>
                <div style={{paddingLeft:"40%", paddingRight: "40%", paddingTop: "1%", paddingBottom: "1%"}}>
                    <Form.Group >
                        <Form.Control onChange={(event) => this.setState({permissions: event.target.value})} as="select" defaultValue="Choose permission...">
                            <option>Choose permission...</option>
                            <option value="ADD_PRODUCT_TO_STORE">Add product to store</option>
                            <option value="REMOVE_PRODUCT_FROM_STORE">Remove product from store</option>
                            <option value="UPDATE_PRODUCT_PRICE">Update product price</option>

                            <option value="VIEW_DISCOUNT_POLICY">View discount policy</option>
                            <option value="VIEW_PURCHASE_POLICY">View purchase policy</option>

                            <option value="ADD_DISCOUNT_RULE">Add discount rule</option>
                            <option value="ADD_PURCHASE_RULE">Add purchase rule</option>
                            <option value="REMOVE_DISCOUNT_RULE">Remove discount rule</option>
                            <option value="REMOVE_PURCHASE_RULE">Remove purchase rule</option>

                            <option value="APPOINT_OWNER">Appoint owner</option>
                            <option value="REMOVE_OWNER_APPOINTMENT">Remove owner appointment</option>
                            <option value="APPOINT_MANAGER">Appoint manager</option>
                            <option value="ADD_PERMISSION">Add permission</option>
                            <option value="REMOVE_PERMISSION">Remove permission</option>
                            <option value="REMOVE_MANAGER_APPOINTMENT">Remove manager appointment</option>
                            <option value="RECEIVE_STORE_WORKER_INFO">Receive store worker info</option>
                            <option value="RECEIVE_STORE_HISTORY">Receive store history</option>

                            <option value="RECEIVE_STORE_REVENUE">Receive store revenue</option>
                            <option value="REPLY_TO_BID">Reply to bid</option>
                        </Form.Control>
                    </Form.Group>
                </div>
                {/*<div> <label> Permissions* : <input className = "permissions" type = "text" placeholder='OPEN_STORE, REVIEW_PRODUCT,...' style={{width: "370px"}} onChange = {(e) => this.handleInputChange(e, 'permissions')}/> </label> </div>*/}
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Add Permission </button>
            </form>
        )
    }
}

export default AddPermission;