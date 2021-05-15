import React, { useState } from "react";
import ReactDOM from "react-dom";
import AddProduct from "../ProductPages/AddProduct";
import DeleteProduct from "../ProductPages/DeleteProduct";
import EditProduct from "../ProductPages/EditProduct";
import AddDiscount from "../DiscountPurchasePolicyPages/AddDiscount";
import AppointOwner from "../AppoinmentPages/AppointOwner";
import RemoveOwner from "../AppoinmentPages/RemoveOwner";
import AddPermission from "../PermissionsPages/AddPermission";
import DeletePermission from "../PermissionsPages/DeletePermission";
import AppointManager from "../AppoinmentPages/AppointManager";
import RemoveManager from "../AppoinmentPages/RemoveManager";
import WorkerDetails from "../ReportsPages/WorkerDetails";
import StorePurchaseHistory from "../ReportsPages/StorePurchaseHistory";
import UserPurchaseHistory from "../ReportsPages/UserPurchaseHistory";
import StaticUserInfo from "../API/StaticUserInfo";
import {Link} from "react-router-dom";
import Connection from "../API/Connection";

class StoreManagment extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: StaticUserInfo.getUsername(),
            storeId: StaticUserInfo.getStoreId(),
            permissions: '',
            //permissions: ['ADD_PRODUCT_TO_STORE','REMOVE_PRODUCT_FROM_STORE'],
        };
        //this.handleSearchResponse = this.handleSearchResponse.bind(this);
    }

    componentWillMount() {
        //console.log('mounted!!!!!!!!!!!')
        Connection.sendGetPermissionsRequest('getUserPermissions',this.state.username,this.state.storeId).then(this.handleGetPermissionsResponse, Connection.handleReject);
    }

    /* result here is list of permissions (enums) */
    handleGetPermissionsResponse(result){
        if(!result.response.isFailure){
            //var arrayOfPerm = result.response.result
            console.log('the result isss:  ' + result)
            //this.setState({permissions: arrayOfPerm});
        }
        else{
            alert(result.response.errMsg);
        }
    }

    handleClick(perm, e) {
        e.preventDefault();

        perm === 'ADD_PRODUCT_TO_STORE' ? ReactDOM.render(<React.StrictMode><AddProduct /></React.StrictMode>, document.getElementById('root')) :
            perm === 'REMOVE_PRODUCT_FROM_STORE' ? ReactDOM.render(<React.StrictMode><DeleteProduct /></React.StrictMode>, document.getElementById('root')) :
                perm === 'UPDATE_PRODUCT_PRICE' ? ReactDOM.render(<React.StrictMode><EditProduct /></React.StrictMode>, document.getElementById('root')) :
                    perm === 'ADD_DISCOUNTS' ? ReactDOM.render(<React.StrictMode><AddDiscount /></React.StrictMode>, document.getElementById('root')) :
                        //perm === ' DELETE_DISCOUNTS' ? ReactDOM.render(<React.StrictMode><DeleteDiscount /></React.StrictMode>, document.getElementById('root')) :
                        perm === 'APPOINT_OWNER' ? ReactDOM.render(<React.StrictMode><AppointOwner /></React.StrictMode>, document.getElementById('root')) :
                            perm === 'REMOVE_OWNER_APPOINTMENT' ? ReactDOM.render(<React.StrictMode><RemoveOwner /></React.StrictMode>, document.getElementById('root')) :
                                //perm === 'ADD_PERMISSION' ? ReactDOM.render(<React.StrictMode><AddPermission /></React.StrictMode>, document.getElementById('root')) :
                                //perm === 'DELETE_PERMISSION' ? ReactDOM.render(<React.StrictMode><DeletePermission /></React.StrictMode>, document.getElementById('root')) :
                                //perm === 'APPOINT_MANAGER' ? ReactDOM.render(<React.StrictMode><AppointManager /></React.StrictMode>, document.getElementById('root')) :
                                perm === 'REMOVE_MANAGER_APPOINTMENT' ? ReactDOM.render(<React.StrictMode><RemoveManager /></React.StrictMode>, document.getElementById('root')) :
                                    perm === 'REMOVE_MANAGER_APPOINTMENT' ? ReactDOM.render(<React.StrictMode><RemoveManager /></React.StrictMode>, document.getElementById('root')) :
                                        perm === 'RECEIVE_STORE_WORKER_INFO' ? ReactDOM.render(<React.StrictMode><WorkerDetails /></React.StrictMode>, document.getElementById('root')) :
                                            perm === 'RECEIVE_STORE_HISTORY' ? ReactDOM.render(<React.StrictMode><StorePurchaseHistory /></React.StrictMode>, document.getElementById('root')) :
                                                //    perm === 'RECEIVE_STORE_WORKER_INFO' ? ReactDOM.render(<React.StrictMode><UserPurchaseHistory /></React.StrictMode>, document.getElementById('root')) :
                                                console.log('warning')
    }

    render() {
        return (
            <div>
                {this.state.permissions.map((perm) => {
                    return <div><Link to={perm}><button>{perm.toString().replaceAll('_',' ')}</button></Link></div>
                })}
            </div>)
    }
}

// key={perm} onClick={(e) => this.handleClick(perm, e)}> {perm.toString().replaceAll('_',' ')}

export default StoreManagment;