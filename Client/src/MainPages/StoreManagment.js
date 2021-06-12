import React, { useState } from "react";
import ReactDOM from "react-dom";
import AddProduct from "../ProductPages/AddProduct";
import DeleteProduct from "../ProductPages/DeleteProduct";
import EditProduct from "../ProductPages/EditProduct";
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
import {Button, Spinner} from "react-bootstrap";
import MainDiscountPolicyPage from "../DiscountPurchasePolicyPages/MainDiscountPolicyPage";
import DeleteDiscountRule from "../DiscountPurchasePolicyPages/DeleteDiscountRule";
import StoreRevenue from "../ReportsPages/StoreRevenue";
import SystemRevenue from "../ReportsPages/SystemRevenue";
import MainPurchasePolicyPage from "../DiscountPurchasePolicyPages/MainPurchasePolicyPage";
import DiscountsDetails from "../ReportsPages/DiscountsDetails";
import PurchaseDetails from "../ReportsPages/PurchaseDetails";
import RemoveDiscountRule from "../DiscountPurchasePolicyPages/RemoveDiscountRule";
import RemovePurchaseRule from "../DiscountPurchasePolicyPages/RemovePurchaseRule";
import OwnerStoreHistory from "../ReportsPages/OwnerStoreHistory";

class StoreManagment extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: window.sessionStorage.getItem('username'),
            storeId: window.sessionStorage.getItem('storeID'),
            permissions: [],
            loaded: false,
            //permissions: ['ADD_PRODUCT_TO_STORE','REMOVE_PRODUCT_FROM_STORE'],
        };
        //this.handleSearchResponse = this.handleSearchResponse.bind(this);
        this.handleGetPermissionsResponse = this.handleGetPermissionsResponse.bind(this);
    }

    componentDidMount() {
        //console.log('mounted!!!!!!!!!!!')
        Connection.sendGetPermissionsRequest('getUserPermissions',this.state.username,this.state.storeId).then
        (this.handleGetPermissionsResponse, Connection.handleReject)
    }

    /* result here is list of permissions (enums) */
    handleGetPermissionsResponse(result){

        if(!result.isFailure){
            var arrayOfPerm = result.result
            this.setState({loaded: true});
            //console.log('the result isss:  ' + result.response.result)
            //console.log('the result isss:  ' + ['asd', 'asd'])
            this.setState({permissions: arrayOfPerm});
        }
        else{
            alert(result.errMsg);
        }
    }

    handleClick(perm, e) {
        e.preventDefault();

        perm === 'ADD_PRODUCT_TO_STORE' ? ReactDOM.render(<React.StrictMode><AddProduct /></React.StrictMode>, document.getElementById('root')) :
            perm === 'REMOVE_PRODUCT_FROM_STORE' ? ReactDOM.render(<React.StrictMode><DeleteProduct /></React.StrictMode>, document.getElementById('root')) :
                perm === 'UPDATE_PRODUCT_INFO' ? ReactDOM.render(<React.StrictMode><EditProduct /></React.StrictMode>, document.getElementById('root')) :
                    perm === 'ADD_DISCOUNT_RULE' ? ReactDOM.render(<React.StrictMode><MainDiscountPolicyPage /></React.StrictMode>, document.getElementById('root')) :
                        perm === ' REMOVE_DISCOUNT_RULE' ? ReactDOM.render(<React.StrictMode><DeleteDiscountRule /></React.StrictMode>, document.getElementById('root')) :
                            perm === 'APPOINT_OWNER' ? ReactDOM.render(<React.StrictMode><AppointOwner /></React.StrictMode>, document.getElementById('root')) :
                                perm === 'REMOVE_OWNER_APPOINTMENT' ? ReactDOM.render(<React.StrictMode><RemoveOwner /></React.StrictMode>, document.getElementById('root')) :
                                    perm === 'ADD_PERMISSION' ? ReactDOM.render(<React.StrictMode><AddPermission /></React.StrictMode>, document.getElementById('root')) :
                                        perm === 'REMOVE_PERMISSION' ? ReactDOM.render(<React.StrictMode><DeletePermission /></React.StrictMode>, document.getElementById('root')) :
                                            perm === 'APPOINT_MANAGER' ? ReactDOM.render(<React.StrictMode><AppointManager /></React.StrictMode>, document.getElementById('root')) :
                                                perm === 'REMOVE_MANAGER_APPOINTMENT' ? ReactDOM.render(<React.StrictMode><RemoveManager /></React.StrictMode>, document.getElementById('root')) :
                                                    perm === 'RECEIVE_STORE_REVENUE' ? ReactDOM.render(<React.StrictMode><StoreRevenue /></React.StrictMode>, document.getElementById('root')) :
                                                        perm === 'RECEIVE_GENERAL_REVENUE' ? ReactDOM.render(<React.StrictMode><SystemRevenue /></React.StrictMode>, document.getElementById('root')) :
                                                            perm === 'RECEIVE_STORE_HISTORY' ? ReactDOM.render(<React.StrictMode><OwnerStoreHistory /></React.StrictMode>, document.getElementById('root')) :
                                                                perm === 'RECEIVE_STORE_WORKER_INFO' ? ReactDOM.render(<React.StrictMode><WorkerDetails /></React.StrictMode>, document.getElementById('root')) :
                                                                    perm === 'ADD_PURCHASE_RULE' ? ReactDOM.render(<React.StrictMode><MainPurchasePolicyPage /></React.StrictMode>, document.getElementById('root')) :
                                                                        perm === 'VIEW_DISCOUNT_POLICY' ? ReactDOM.render(<React.StrictMode><DiscountsDetails /></React.StrictMode>, document.getElementById('root')) :
                                                                            perm === 'VIEW_PURCHASE_POLICY' ? ReactDOM.render(<React.StrictMode><PurchaseDetails /></React.StrictMode>, document.getElementById('root')) :
                                                                                perm === 'REMOVE_DISCOUNT_RULE' ? ReactDOM.render(<React.StrictMode><RemoveDiscountRule /></React.StrictMode>, document.getElementById('root')) :
                                                                                    perm === 'REMOVE_PURCHASE_RULE' ? ReactDOM.render(<React.StrictMode><RemovePurchaseRule /></React.StrictMode>, document.getElementById('root')) :
                                                console.log('warning')

    }

    render() {
        return (
            <div>
                {!this.state.loaded &&
                <Spinner animation="border" />
                    // <Spinner
                    //     as="span"
                    //     animation="grow"
                    //     size="sm"
                    //     role="status"
                    //     aria-hidden="true"
                    // />
                }
                {this.state.loaded && this.state.permissions.map((perm) => {
                    return <div><Link to={perm}><button>{perm.toString().replaceAll('_',' ')}</button></Link></div>
                })}
            </div>)
    }
}

// key={perm} onClick={(e) => this.handleClick(perm, e)}> {perm.toString().replaceAll('_',' ')}

export default StoreManagment;