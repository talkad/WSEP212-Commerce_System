import React from "react";
import ProductEntry from "../Components/ProductEntry";
import ReviewProduct from "./ReviewProduct";
import Connection from "../API/Connection";
import ProductEntryHistory from "../Components/ProductEntryHistory";
import {Accordion, Button, Card, CardGroup, Image, Spinner, Table} from "react-bootstrap";
import ProductEntryCart from "../Components/ProductEntryCart";
import buy_something from "../Images/harold_buy_something.png"
import * as Icon from "react-bootstrap-icons";
import harold_love from "../Images/harold_love.png";
import Modal from "react-modal";


const products = [
    {
        name: "brioche",
        productID: 1,
        storeID: 1,
        price: 50.5,
        seller: "ma'afia",
        categories: ["pastry", "tasty"],
        rating: 5,
        numReview: 200,
    },
    {
        name: "eclair",
        productID: 2,
        storeID: 2,
        price: 50.5,
        seller: "ma'afia2",
        categories: ["pastry", "tasty"],
        rating: 4.5,
        numReview: 300,
    }
]

class PurchaseHistory extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            purchaseHistory: [],
            loaded: false,

            reviewModal: false,
            reviewModalProduct: '',
            reviewModalStore: '',
            reviewModalProductID: '',
            reviewModalStoreID: '',
        }

        this.counter = 1;

        this.handleResponse = this.handleResponse.bind(this);
        this.handleContinue = this.handleContinue.bind(this);
        this.reviewProduct = this.reviewProduct.bind(this);
    }

    handleResponse(result) {
        if (!result.isFailure) {
            this.setState({purchaseHistory: result.result, loaded: true});
        } else {
            alert(result.errMsg);
            this.props.history.goBack();
        }
    }

    componentDidMount() {
        Connection.sendGetPurchaseHistory().then(this.handleResponse, Connection.handleReject);
    }

    handleContinue(){
        this.setState({reviewModal: false, reviewModalProduct: '', reviewModalStore: '',
            reviewModalProductID: '', reviewModalStoreID: '',});
    }

    reviewProduct(productName, storeName, productID, storeID){
        this.setState({reviewModal: true, reviewModalProduct: productName, reviewModalStore: storeName,
            reviewModalProductID: productID, reviewModalStoreID: storeID})
    }

    render() {
        const zip = (a, b) => a.map((k, i) => [k, b[i]]);
        return (
            <div id="purchase_history_page">
                <Modal
                    style={{
                        overlay: {

                            position: 'absolute',
                            top: '50%',
                            left: '50%',
                            transform: 'translate(-50%, -50%)',
                            backgroundColor: 'rgba(255, 255, 255, 0.75)'
                        },
                    }}

                    isOpen={this.state.reviewModal}
                    onRequestClose={this.handleContinue}
                    contentLabel="Example Modal"
                >
                    <Icon.XCircle onClick={this.handleContinue}/>
                    <ReviewProduct
                        product={this.state.reviewModalProduct}
                        store={this.state.reviewModalStore}
                        productID={this.state.reviewModalProductID}
                        storeID={this.state.reviewModalStoreID}
                        continueHandler={this.handleContinue}
                    />
                </Modal>

                <div id="purchase_history_title">
                    <h1>Purchase History</h1>
                </div>

                {this.state.loaded && (this.state.purchaseHistory.length === 0) &&
                <Image src={buy_something}/>}
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
                                                    <th>Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                            {zip(basket.productsDTO, basket.amounts).map( entry => (
                                                <tr>
                                                    <td>{entry[0].name}</td>
                                                    <td>{basket.storeName}</td>
                                                    <td>{entry[0].price}</td>
                                                    <td>{entry[1]}</td>
                                                    <td><Button onClick={() => this.reviewProduct(entry[0].name, basket.storeName, entry[0].productID, basket.storeID)}
                                                                variant="link">Review product</Button></td>
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

export default PurchaseHistory;

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