import React from "react";
import ProductEntry from "../Components/ProductEntry";
import Connection from "../API/Connection";
import ProductEntryHistory from "../Components/ProductEntryHistory";
import {Accordion, Button, Card, CardGroup, FormControl, Image, InputGroup, Spinner, Table} from "react-bootstrap";
import ProductEntryCart from "../Components/ProductEntryCart";
import stonks_down from "../Images/stonks_down.jpg"
import * as Icon from "react-bootstrap-icons";
import harold_love from "../Images/harold_love.png";

class StorePurchaseHistory extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            storeID: '',
            purchaseHistory: [],
            loaded: true,
            afterSearch: false,
        }

        this.counter = 1;

        this.handleResponse = this.handleResponse.bind(this);
        this.onChangeStoreID = this.onChangeStoreID.bind(this);
        this.getPurchaseHistory = this.getPurchaseHistory.bind(this);
    }

    handleResponse(result) {
        if (!result.isFailure) {
            this.setState({purchaseHistory: result.result, loaded: true, afterSearch: true});
        } else {
            alert(result.errMsg);
            this.props.history.goBack();
        }
    }

    onChangeStoreID(event){
        this.setState({storeID: event.target.value})
    }

    getPurchaseHistory(){
        Connection.sendStoreHistoryRequest(this.state.storeID).then(this.handleResponse, Connection.handleReject)
        this.setState({loaded: false, afterSearch: false});
    }

    render() {
        const zip = (a, b) => a.map((k, i) => [k, b[i]]);
        return (
            <div id="purchase_history_page">
                <div id="purchase_history_title">
                    <h1>Store Purchase History</h1>
                </div>

                <div style={{paddingBottom: "2%", paddingTop: "2%", paddingRight: "30%", paddingLeft: "30%"}}>
                    <InputGroup className="mb-3">
                        <FormControl
                            onChange={this.onChangeStoreID}
                            placeholder="Store ID"
                        />
                        <InputGroup.Append>
                            <Button onClick={this.getPurchaseHistory} variant="outline-secondary">Show</Button>
                        </InputGroup.Append>
                    </InputGroup>
                </div>

                {this.state.afterSearch && (this.state.purchaseHistory.length === 0) && <Image src={stonks_down}/>}
                {!this.state.loaded && <Spinner animation="grow"/>}
                <div>
                    {this.state.loaded && <Accordion id="purchase_history_contents" style={{width: "100%"}} defaultActiveKey={this.counter}>
                        {this.state.purchaseHistory.map(({basket, totalPrice, purchaseDate}) =>
                            <Card className="purchase_history_card" style={{marginTop: "10px", width: "50%"}}>
                                <Accordion.Toggle as={Card.Header} eventKey={this.counter}>
                                    Order from: {purchaseDate}, Total: {totalPrice}
                                </Accordion.Toggle>
                                <Accordion.Collapse eventKey={this.counter++}>
                                    <Card.Body>
                                        <Table striped bordered hover>
                                            <thead>
                                            <tr>
                                                <th>Product</th>
                                                <th>Seller</th>
                                                <th>Price</th>
                                                <th>Quantity</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            {zip(basket.productsDTO, basket.amounts).map( entry => (
                                                <tr>
                                                    <td>{entry[0].name}</td>
                                                    <td>{basket.storeName}</td>
                                                    <td>{entry[0].price}</td>
                                                    <td>{entry[1]}</td>
                                                </tr>
                                            ))}
                                            </tbody>
                                        </Table>
                                    </Card.Body>
                                </Accordion.Collapse>
                            </Card>
                        )}
                    </Accordion>}
                </div>
            </div>
        );
    }
}

export default StorePurchaseHistory;

// {this.state.loaded && this.state.purchaseHistory.map(({basket, totalPrice, purchaseDate}) => (
//     <div>
//         <h2>Purchase from: {purchaseDate}</h2>
//         <h2>Total price: {totalPrice}</h2>
//
//         <CardGroup>
//             {zip(basket.productsDTO, basket.amounts).map( entry => (
//                 <div>
//                     <ProductEntryHistory
//                         name={entry[0].name}
//                         price={entry[0].price}
//                         seller={basket.storeName}
//                         productID={entry[0].productID}
//                         storeID={basket.storeID}
//                         amount={entry[1]}
//                     />
//                 </div>
//             ) ) }
//         </CardGroup>
//
//     </div>
// ))}

// ([
//     <tr data-toggle="collapse" data-target={'#' + this.counter.toString()}>
//         <td>{this.counter++}</td>
//         {/* todo: see about this later */}
//         <td>{purchaseDate}</td>
//         <td>{totalPrice}</td>
//         <td>
//             <Button>Hello there</Button>
//         </td>
//     </tr>,
//     <tr>
//         <td colSpan="4" className="hiddentablerow">
//             <div id={'#' + this.counter.toString()}>
//                 Hello there
//             </div>
//         </td>
//     </tr>
// ])



// import React from "react";
// import StaticUserInfo from "../API/StaticUserInfo";
// import Connection from "../API/Connection";
//
// class StorePurchaseHistory extends React.Component{
//     constructor(props) {
//         super(props);
//         this.state = {
//             functionName: 'getStorePurchaseHistory',
//             adminName: '',
//             storeId: '',
//             showMessage: false,
//             toShow: ''
//         };
//         this.handleReportResponse = this.handleReportResponse.bind(this)
//     }
//
//     onButtonClickHandler = (e) => {
//         e.preventDefault();
//
//         Connection.sendStoreHistoryRequest(this.state.functionName, this.state.adminName, this.state.storeId).then(this.handleReportResponse, Connection.handleReject)
//
//         this.setState({showMessage: true});
//         //e.preventDefault();
//     };
//
//     handleReportResponse(result){
//         if(!result.isFailure){
//             let show = "";
//             let ProductCounter = 1;
//
//             result.result.forEach(element => show = show.concat(
//                 "Purchase Num: " + ProductCounter++ + " Price: " + element.totalPrice.toString() + " Date: " + element.purchaseDate.toString() + "  :::  "));
//
//             this.setState({toShow: show});
//         }
//         else{
//             alert(result.errMsg);
//         }
//     }
//
//     handleInputChange = (e, name) => {
//         this.setState({
//             [name]: e.target.value
//         })
//     }
//
//     render(){
//         return(
//             <form>
//                 <h1>Purchase History Details For Store Page </h1>
//                 <div> <label> Admin Name : <input className = "adminName" type = "text" onChange = {(e) => this.handleInputChange(e, 'adminName')}/> </label> </div>
//                 <div> <label> Store Id : <input className = "storeId" type = "text" onChange = {(e) => this.handleInputChange(e, 'storeId')}/> </label> </div>
//                 <div className="toShow"> {this.state.showMessage && <p> {this.state.toShow} </p>}
//                     <button type = "button" onClick = {(e) => this.onButtonClickHandler(e)}> Show History </button>
//                 </div>
//             </form>
//         )
//     }
// }
//
// export default StorePurchaseHistory;