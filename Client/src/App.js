import logo from './logo.svg';
import './App.css';
import React from "react";
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
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
import Connection from "./API/Connection";

// var W3CWebSocket = require('websocket').w3cwebsocket;
//
// var client = new W3CWebSocket('ws://localhost:80/', 'echo-protocol');

// const client = new WebSocket('http://localhost:8080');

// let WebSocketClient = require('websocket').client;

// let client = new WebSocketClient();

// var W3CWebSocket = require('ws')
//
// var client = new W3CWebSocket('ws://localhost:8080/', 'echo-protocol');


// client.connect('ws://localhost:8080/', 'echo-protocol');

// Connection.setConnection(client);

let client = new WebSocket("ws://localhost:8080/ws");

Connection.setConnection(client);

// if(!result.response.isFailure){
//
// }
// else{
//     alert(result.response.errMsg);
// }

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

    // constructor(props) {
    //     super(props);
    //     this.state = {
    //         identifier: '',
    //         username: '',
    //         password: '',
    //         flag: false,
    //     };
    //
    //     this.handleUsernameChange = this.handleUsernameChange.bind(this);
    //     this.handlePasswordChange = this.handlePasswordChange.bind(this);
    //     this.handleLogin = this.handleLogin.bind(this);
    //     this.handleRegister = this.handleRegister.bind(this);
    // }
    //
    // componentDidMount() {
    //     client.onopen = () => {
    //         console.log('WebSocket Client Connected');
    //     };
    //     client.onmessage = (message) => {
    //         console.log(message);
    //         const dataFromServer = JSON.parse(message.data);
    //         if(dataFromServer.type === "identification"){
    //             this.setState({identifier: dataFromServer.identifier});
    //         }
    //
    //         if(dataFromServer.response === "login"){
    //             if(dataFromServer.isFailure === true){
    //                 alert(`registration failed. Error: ${dataFromServer.err}` )
    //             }
    //             else{
    //                 this.setState({identifier: dataFromServer.result})
    //                 alert("registered successfully");
    //             }
    //         }
    //
    //         if(dataFromServer.response === "register"){
    //             if(dataFromServer.isFailure === true){
    //                 alert(`login failed. Error: ${dataFromServer.err}` )
    //             }
    //             else{
    //                 this.setState({identifier: dataFromServer.result});
    //                 alert('logged in');
    //             }
    //         }
    //     }
    // }
    //
    // handleUsernameChange(event) {
    //     this.setState({username: event.target.value});
    // }
    //
    // handlePasswordChange(event) {
    //     this.setState({password: event.target.value});
    // }
    //
    // handleLogin(){
    //     console.log(this.state.username);
    //     console.log(this.state.password);
    //     client.send(JSON.stringify({
    //                 "action": "login",
    //                 "identifier": this.state.identifier,
    //                 "username": this.state.username,
    //                 "password": this.state.password
    //             }))
    //     this.setState({username: '', password: ''})
    // }
    //
    // handleRegister(){
    //     console.log(this.state.username);
    //     console.log(this.state.password);
    //     client.send(JSON.stringify({
    //         "action": "register",
    //         "identifier": this.state.identifier,
    //         "username": this.state.username,
    //         "password": this.state.password
    //     }))
    //     this.setState({username: '', password: ''})
    // }

    // render() {
    //     const thisPage = <div className="App">
    //         <header className="App-header">
    //             <form>
    //                 <input type="text" name="username" placeholder="Username" value={this.state.username}
    //                        onChange={this.handleUsernameChange}/>
    //                 <input type="password" name="password" placeholder="Password" value={this.state.password}
    //                        onChange={this.handlePasswordChange}/>
    //             </form>
    //             <form>
    //                 <button type="button" onClick={this.handleLogin}>login</button>
    //                 <button type="button" onClick={this.handleRegister}>register</button>
    //             </form>
    //         </header>
    //     </div>;
    //     return (thisPage);
    // }


}

export default App;
