import './App.css';
import React from "react";
import {BrowserRouter as Router, Link, Route, Switch} from 'react-router-dom';
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
import AppointOwner from "./AppoinmentPages/AppointOwner";
import RemoveOwner from "./AppoinmentPages/RemoveOwner";
import AddPermission from "./PermissionsPages/AddPermission";
import DeletePermission from "./PermissionsPages/DeletePermission";
import AppointManager from "./AppoinmentPages/AppointManager";
import RemoveManager from "./AppoinmentPages/RemoveManager";
import WorkerDetails from "./ReportsPages/WorkerDetails";
import StorePurchaseHistory from "./ReportsPages/StorePurchaseHistory";
import Connection from "./API/Connection";
import ChooseMyStore from "./MainPages/ChooseMyStore";
import {
    Alert,
    Button,
    Container,
    Form,
    FormControl,
    Image,
    InputGroup,
    Nav,
    Navbar,
    NavDropdown,
    NavItem, Spinner
} from "react-bootstrap";
import Home from "./Pages/Home";
import * as Icon from 'react-bootstrap-icons';
import StaticUserInfo from "./API/StaticUserInfo";
import {ReactComponent as OurLogo} from "./Images/logo.svg";
import CategoryDiscountRule from "./DiscountPurchasePolicyPages/CategoryDiscountRule";
//import AndCompositionDiscountRule from "./DiscountPurchasePolicyPages/NoNeedForNow/AndCompositionDiscountRule";
import CompositionPoliciesPage from "./DiscountPurchasePolicyPages/CompositionPoliciesPage";
import ConditionalCategoryDiscountRule from "./DiscountPurchasePolicyPages/ConditionalCategoryDiscountRule";
import ConditionalProductDiscountRule from "./DiscountPurchasePolicyPages/ConditionalProductDiscountRule";
import ConditionalStoreDiscountRule from "./DiscountPurchasePolicyPages/ConditionalStoreDiscountRule";
import DeleteDiscountRule from "./DiscountPurchasePolicyPages/DeleteDiscountRule";
import DeletePurchaseRule from "./DiscountPurchasePolicyPages/DeletePurchaseRule";
import MainDiscountPolicyPage from "./DiscountPurchasePolicyPages/MainDiscountPolicyPage";
import ProductDiscountRule from "./DiscountPurchasePolicyPages/ProductDiscountRule";
import StoreDiscountRule from "./DiscountPurchasePolicyPages/StoreDiscountRule";
import {A} from "react-select/dist/index-4bd03571.esm";
import Disconnected from "./Pages/Disconnected";


let client = new WebSocket("ws://localhost:8080/ws");

Connection.setConnection(client);

class App extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            visitor: true,
            registered: false,
            storeOwner: false,

            showAlert: false,
            alertVariant: '',
            alertInfo: '',
            showPurchaseButton: false,
            offerUserName: '',
            offerUserProductName: '',
            offerUserProductID: '',
            offerUserStoreID: '',
            offerUserPriceOffer: '',



            showUserAlert: false,
            offerUserAlertVariant: '',
            alertUserInfo: '',

            showManagerAlert: false,
            offerManagerAlertVariant: 'primary',
            offerManagerName: '',
            offerManagerProductName: '',
            offerManagerProductID: '',
            offerManagerStoreID: '',
            offerManagerPriceOffer: '',
            counterOffer: '',
        }

        this.handleLogout = this.handleLogout.bind(this)
        this.handleLogoutResponse = this.handleLogoutResponse.bind(this);
        this.handleStoreOwnedResponse = this.handleStoreOwnedResponse.bind(this);
        this.handleOfferNotification = this.handleOfferNotification.bind(this);
        this.handleManagerOfferAccept = this.handleManagerOfferAccept.bind(this);
        this.handleManagerOfferReject = this.handleManagerOfferReject.bind(this);
        this.handleManagerReplyResponse = this.handleManagerReplyResponse.bind(this);
        this.onChangeCounterOffer = this.onChangeCounterOffer.bind(this);
        this.handleAcceptOffer = this.handleAcceptOffer.bind(this);
        this.handleManagerReply = this.handleManagerReply.bind(this);
    }

    componentDidMount() {
        let username = window.sessionStorage.getItem('username');
        if (username !== '' && username !== null && username.substr(0, 5) !== "Guest") {
            if(StaticUserInfo.getUserStores().length === 0){
                this.setState({visitor: false, registered: true, storeOwner: false});
            }
            else{
                this.setState({visitor: false, registered: true, storeOwner: true});
            }
        }
        else{
            this.setState({visitor: true, registered: false, storeOwner: false});
        }

        if(window.sessionStorage.getItem('username') !== null) {
            Connection.sendStoreOwned().then(this.handleStoreOwnedResponse, Connection.handleReject);
        }

        Connection.getOfferNotification().then(this.handleOfferNotification, Connection.handleReject);
    }


    handleOfferNotification(result){
        if(result.action === "bidOffer"){
            let parsed = JSON.parse(result.message);
            this.setState({showManagerAlert: true, offerManagerName: parsed.name,
                offerManagerProductName: parsed.productName, offerManagerProductID: parsed.productID,
                offerManagerStoreID: parsed.storeID, offerManagerPriceOffer: parsed.priceOffer});
        }
        else if(result.action === "changeOfferStatusAccepted"){
            let parsed = JSON.parse(result.message);
            this.setState({showUserAlert: true, offerUserAlertVariant: 'primary',
                alertUserInfo: `Your offer for ${parsed.productName} from ${parsed.name} got accepted.`,
                showPurchaseButton: true, offerUserName: parsed.name, offerUserProductName: parsed.productName,
                offerUserProductID: parsed.productID, offerUserStoreID: parsed.storeID, offerUserPriceOffer: parsed.priceOffer});
        }
        else if(result.action === "changeOfferStatusDeclined"){
            this.setState({showUserAlert: true, offerUserAlertVariant: 'danger', alertUserInfo: result.message,
                showPurchaseButton: false});
        }
        else if(result.action === "changeOfferStatus"){
            let parsed = JSON.parse(result.message);
            this.setState({showUserAlert: true, offerUserAlertVariant: 'primary',
                alertUserInfo: `You got a counter offer for ${parsed.productName} from ${parsed.name}. They offered ${parsed.priceOffer}`,
                showPurchaseButton: true, offerUserName: parsed.name, offerUserProductName: parsed.productName,
                offerUserProductID: parsed.productID, offerUserStoreID: parsed.storeID, offerUserPriceOffer: parsed.priceOffer});
        }

        Connection.getOfferNotification().then(this.handleOfferNotification, Connection.handleReject);
    }

    //-----------------------------------MANAGER OFFER START--------------------------------------------------------
    handleManagerReplyResponse(result){
        if(!result.isFailure){
            alert("reply sent");
            this.setState({showManagerAlert: false, offerManagerName: '', offerManagerProductName: '',
                offerManagerProductID: '', offerManagerStoreID: '', offerManagerPriceOffer: ''})
        }
        else{
            alert(result.errMsg);
        }
    }

    handleManagerOfferAccept(){
        Connection.sendManagerOfferReply(this.state.offerManagerName, this.state.offerManagerProductID,
            this.state.offerManagerStoreID, "-1").then(this.handleManagerReplyResponse, Connection.handleReject);
    }

    handleManagerOfferReject(){
        Connection.sendManagerOfferReply(this.state.offerManagerName, this.state.offerManagerProductID,
            this.state.offerManagerStoreID, "-2").then(this.handleManagerReplyResponse, Connection.handleReject);
    }

    onChangeCounterOffer(event){
        this.setState({counterOffer: event.target.value})
    }

    handleManagerReply(){
        let value = parseInt(this.state.counterOffer);

        if(value >= 0) {
            Connection.sendManagerOfferReply(this.state.offerManagerName, this.state.offerManagerProductID,
                this.state.offerManagerStoreID, this.state.counterOffer).then(this.handleManagerReplyResponse, Connection.handleReject);
        }
        else{
            alert("counter offer must be a natural number");
        }
    }

    //-----------------------------------MANAGER OFFER END----------------------------------------------------------

    //-----------------------------------USER OFFER START-----------------------------------------------------------

    handleDismissOffer(){
        this.setState({showUserAlert: false, offerUserAlertVariant: '', alertUserInfo: '',
            showPurchaseButton: false});
    }

    handleAcceptOffer(){
        window.location.href = `http://localhost:3000/checkout/?storeName=${this.state.offerUserName}&productName=${this.state.offerUserProductName}&productID=${this.state.offerUserProductID}&storeID=${this.state.offerUserStoreID}`
    }

    //-----------------------------------USER OFFER END-------------------------------------------------------------

    handleStoreOwnedResponse(result){
        if(!result.isFailure){
            if(result.result.length !== 0){
                this.setState({storeOwner: true});
            }
            else{
                this.setState({storeOwner: false});
            }
        }
    }

    handleLogoutResponse(result){
        if(!result.isFailure){
            window.sessionStorage.removeItem('username');
            this.setState({showAlert: true, alertVariant: 'success', alertInfo: 'Logged out'});
            window.location.href = '/';
            //window.location.reload();
        }
        else{
            this.setState({showAlert: true, alertVariant: 'danger', alertInfo: result.errMsg});
        }
    }

    handleLogout() {
        Connection.sendLogout().then(this.handleLogoutResponse, Connection.handleReject);
    }

    render() {
        return(
            <Router>
                <Navbar bg="light" expand="lg">
                    <Navbar.Brand href="/">
                        <OurLogo height={50} width={150}/>
                    </Navbar.Brand>
                    <Navbar.Toggle aria-controls="basic-navbar-nav" />
                    {(window.location.href !== 'http://localhost:3000/Disconnected') && <Navbar.Collapse id="basic-navbar-nav">
                        <Nav className="mr-auto">

                            {this.state.registered &&<Navbar.Text style={{color: "black"}}> Hi, {window.sessionStorage.getItem('username')}</Navbar.Text>}

                            <Nav.Link href="/">Home</Nav.Link>

                            {this.state.visitor &&
                            <Navbar.Text>
                                <Link to="/login">Sign in</Link> or <Link to="/register">register</Link>
                            </Navbar.Text>}

                            {this.state.registered &&
                            <NavDropdown id={"registered-nav-dropdown"} title={"More"}>
                                <NavDropdown.Item href="/purchaseHistory">Purchase history</NavDropdown.Item>
                                <NavDropdown.Item href="/createStore">Open your own store</NavDropdown.Item>
                            </NavDropdown>}

                            {this.state.storeOwner &&
                            <Nav.Link href="/choosemystore">Manage stores</Nav.Link>}

                        </Nav>
                        <Nav>
                            <Nav.Link href="/cart"><Icon.Cart/></Nav.Link>

                            {this.state.registered &&
                            <Nav.Link onClick={this.handleLogout}>Logout</Nav.Link>}
                        </Nav>
                    </Navbar.Collapse>}

                </Navbar>

                <Alert dismissible show={this.state.showUserAlert} variant={this.state.offerUserAlertVariant} onClose={() => this.setState({showUserAlert: false})}>
                    <Alert.Heading>{this.state.alertUserInfo}</Alert.Heading>
                    {this.state.showPurchaseButton && <div>
                        <hr/>
                        <Button onClick={this.handleAcceptOffer} variant='success'>Purchase</Button>
                        <Button onClick={this.handleDismissOffer} variant='danger'>Dismiss</Button>
                    </div>}
                </Alert>

                <Alert show={this.state.showManagerAlert} variant={this.state.offerManagerAlertVariant} onClose={() => this.setState({showManagerAlert: false})}>
                    <Alert.Heading>{this.state.offerManagerName} sent a price offer!</Alert.Heading>
                    <p>
                        He/She/It/Attack Heli offered to buy {this.state.offerManagerProductName} for {this.state.offerManagerPriceOffer}
                    </p>
                    <hr/>
                    <Button variant="success" onClick={this.handleManagerOfferAccept}>
                        Accept offer
                    </Button>
                    <Button variant="danger" onClick={this.handleManagerOfferReject}>
                        Decline offer
                    </Button>
                    <InputGroup className="mb-3">
                        <FormControl
                            onChange={this.onChangeCounterOffer}
                            name='free-text'
                            placeholder="Counter offer"
                            aria-label="Free search"
                            aria-describedby="basic-addon2"
                        />
                        <InputGroup.Append>
                            <Button onClick={this.handleManagerReply} variant="primary">Offer</Button>
                        </InputGroup.Append>
                    </InputGroup>
                </Alert>

                <div className="App">
                    <Alert show={this.state.showAlert} variant={this.state.alertVariant} onClose={() => this.setState({showAlert: false})}>
                        <Alert.Heading>{this.state.alertInfo}</Alert.Heading>
                    </Alert>
                    <Switch>
                        <Route exact path="/" component={Home}/>} />
                        {/*<Route exact path="/" render={() => <Visitor isVisitor={true}/>} />*/}
                        <Route path="/login" component={Login} />
                        <Route path="/register" component={Register} />
                        {/*<Route path="/registered" component={Registered} />*/}
                        <Route path="/search/" component={SearchResult} />
                        <Route path="/createStore" component={CreateStore}/>
                        <Route path="/purchaseHistory" component={PurchaseHistory}/>
                        <Route path="/cart" component={Cart}/>
                        <Route path="/checkout" component={Checkout}/>
                        <Route path="/storeManagement" component={StoreManagment}/>
                        <Route path="/choosemystore" component={ChooseMyStore}/>
                        <Route path="/ADD_PRODUCT_TO_STORE" component={AddProduct}/>
                        <Route path="/REMOVE_PRODUCT_FROM_STORE" component={DeleteProduct}/>
                        <Route path="/UPDATE_PRODUCT_PRICE" component={EditProduct}/>
                        <Route path="/ADD_DISCOUNT_RULE" component={MainDiscountPolicyPage}/>
                        <Route path="/DELETE_DISCOUNT_RULE" component={DeleteDiscountRule}/>
                        <Route path="/APPOINT_OWNER" component={AppointOwner}/>
                        <Route path="/REMOVE_OWNER_APPOINTMENT" component={RemoveOwner}/>
                        <Route path="/ADD_PERMISSION" component={AddPermission}/>
                        <Route path="/REMOVE_PERMISSION" component={DeletePermission}/>
                        <Route path="/APPOINT_MANAGER" component={AppointManager}/>
                        <Route path="/REMOVE_MANAGER_APPOINTMENT" component={RemoveManager}/>
                        <Route path="/RECEIVE_STORE_WORKER_INFO" component={WorkerDetails}/>
                        <Route path="/RECEIVE_STORE_HISTORY" component={StorePurchaseHistory}/>
                        <Route path="/Category_Discount_Rule" component={CategoryDiscountRule}/>
                        <Route path="/Composition_Policies_Page" component={CompositionPoliciesPage}/>
                        <Route path="/Conditional_Category_Discount_Rule" component={ConditionalCategoryDiscountRule}/>
                        <Route path="/Conditional_Product_Discount_Rule" component={ConditionalProductDiscountRule}/>
                        <Route path="/Conditional_Store_Discount_Rule" component={ConditionalStoreDiscountRule}/>
                        <Route path="/Delete_Discount_Rule" component={DeleteDiscountRule}/>
                        <Route path="/Delete_Purchase_Rule" component={DeletePurchaseRule}/>
                        <Route path="/Product_Discount_Rule" component={ProductDiscountRule}/>
                        <Route path="/Store_Discount_Rule" component={StoreDiscountRule}/>
                        <Route path="/Disconnected" component={Disconnected}/>
                        {/*<Route path="/RECEIVE_STORE_WORKER_INFO" component={RemoveOwner}/>*/}
                    </Switch>
                </div>
            </Router>
        )
    }
}

export default App;
