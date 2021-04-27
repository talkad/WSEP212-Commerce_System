import logo from './logo.svg';
import './App.css';
import React from "react";
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { w3cwebsocket as W3CWebSocket } from 'websocket'
import Visitor from "./Pages/Visitor";
import Login from "./Pages/Login";
import Register from "./Pages/Register";
import Registered from "./Pages/Registered"
import SearchResult from "./Pages/SearchResult";
import CreateStore from "./Pages/CreateStore";
import PurchaseHistory from "./Pages/PurchaseHistory";
import Cart from "./Pages/Cart";
import Checkout from "./Pages/Checkout";
import StoreManagment from "./MainPages/StoreManagment";
import AddProduct from "./ProductPages/AddProduct";
import DeleteProduct from "./ProductPages/DeleteProduct";
import EditProduct from "./ProductPages/EditProduct";
import AddDiscount from "./DiscountPurchasePolicyPages/AddDiscount";
import AppointOwner from "./AppoinmentPages/AppointOwner";
import RemoveOwner from "./AppoinmentPages/RemoveOwner";
import AddPermission from "./PermissionsPages/AddPermission";
import DeletePermission from "./PermissionsPages/DeletePermission";
import AppointManager from "./AppoinmentPages/AppointManager";
import RemoveManager from "./AppoinmentPages/RemoveManager";
import WorkerDetails from "./ReportsPages/WorkerDetails";
import StorePurchaseHistory from "./ReportsPages/StorePurchaseHistory";

const client = new W3CWebSocket('ws://127.0.0.1:8080')

class App extends React.Component{

    render() {
        return(
            <Router>
                <div className="App">
                    <Switch>
                        <Route exact path="/" render={() => <Visitor isVisitor={true}/>} />
                        <Route path="/login" component={Login} />
                        <Route path="/register" component={Register} />
                        <Route path="/registered" component={Registered} />
                        <Route path="/search/" component={SearchResult} />
                        <Route path="/createStore" component={CreateStore}/>
                        <Route path="/purchaseHistory" component={PurchaseHistory}/>
                        <Route path="/cart" component={Cart}/>
                        <Route path="/checkout" component={Checkout}/>
                        <Route path="/storeManagement" component={StoreManagment}/>
                        <Route path="/ADD_PRODUCT_TO_STORE" component={AddProduct}/>
                        <Route path="/REMOVE_PRODUCT_FROM_STORE" component={DeleteProduct}/>
                        <Route path="/UPDATE_PRODUCT_PRICE" component={EditProduct}/>
                        <Route path="/ADD_DISCOUNTS" component={AddDiscount}/>
                        {/*<Route path="/DELETE_DISCOUNTS" component={DeleteDiscount}/>*/}
                        <Route path="/APPOINT_OWNER" component={AppointOwner}/>
                        <Route path="/REMOVE_OWNER_APPOINTMENT" component={RemoveOwner}/>
                        <Route path="/ADD_PERMISSION" component={AddPermission}/>
                        <Route path="/DELETE_PERMISSION" component={DeletePermission}/>
                        <Route path="/APPOINT_MANAGER" component={AppointManager}/>
                        <Route path="/REMOVE_MANAGER_APPOINTMENT" component={RemoveManager}/>
                        <Route path="/RECEIVE_STORE_WORKER_INFO" component={WorkerDetails}/>
                        <Route path="/RECEIVE_STORE_HISTORY" component={StorePurchaseHistory}/>
                        {/*<Route path="/RECEIVE_STORE_WORKER_INFO" component={RemoveOwner}/>*/}
                    </Switch>
                </div>
            </Router>
        )
    }
}

export default App;
