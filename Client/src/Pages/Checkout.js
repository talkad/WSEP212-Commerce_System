import React from "react";
import Connection from "../API/Connection";
import {Alert, Button, Col, Container, Form, InputGroup, Spinner} from "react-bootstrap";
import SupplyDetails from "../JsonClasses/SupplyDetails";
import PaymentDetails from "../JsonClasses/PaymentDetails";

class Checkout extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            name: '',
            address: '',
            city: '',
            country: '',
            zip: '',

            card_number: '',
            expiration: '',
            holder: '',
            ccv: '',
            id: '',

            submitted: false,
            validated: false,

            showAlert: false,
            alertVariant: '',
            alertInfo: '',

            singleProduct: false,
            storeName: '',
            productName: '',
            productID: '',
            storeID: '',
        }

        this.handlePurchase = this.handlePurchase.bind(this);
        this.handleResponse = this.handleResponse.bind(this);
        this.handleChange = this.handleChange.bind(this);
    }

    componentDidMount() {
        const urlParams = new URLSearchParams(window.location.search);
        const storeName = urlParams.get('storeName');
        const productName = urlParams.get("productName");
        const productID = urlParams.get("productID");
        const storeID = urlParams.get("storeID");

        if(storeName !== null){
            this.setState({storeName: storeName, productName: productName, productID: productID,
                storeID: storeID, singleProduct: true});
        }
    }

    handleChange(event) {
        this.setState({[event.target.id]: event.target.value})
    }

    handleResponse(result){
        if(!result.isFailure){
            this.setState({submitted: false,
                showAlert: true, alertVariant: 'success', alertInfo: 'Thank you for your purchase'});
            window.location.href = '/';
        }
        else{
            this.setState({submitted: false,
                showAlert: true, alertVariant: 'danger', alertInfo: result.errMsg});
        }
    }

    handlePurchase(){
        const supplyDetails = new SupplyDetails(this.state.name, this.state.address, this.state.city,
            this.state.country, this.state.zip);
        const paymentDetails = new PaymentDetails(this.state.card_number, this.state.expiration, this.state.ccv,
            this.state.holder, this.state.id);

        if(this.state.singleProduct){
            Connection.sendBuyOffer(this.state.productID, this.state.storeID, paymentDetails, supplyDetails).then(this.handleResponse, Connection.handleReject);
            this.setState({submitted: true});
        }
        else{
            Connection.sendDirectPurchase(paymentDetails, supplyDetails).then(this.handleResponse, Connection.handleReject);
            this.setState({submitted: true});
        }
    }

    render() {
        const handleSubmit = (event) => {
            const form = event.currentTarget;
            event.preventDefault();
            if (form.checkValidity() === false) {
                event.stopPropagation();
            }

            this.setState({validated: true})
            this.handlePurchase();
        };

        return (
            <div className="Login">
                <Container className="Page">
                    <Alert show={this.state.showAlert} variant={this.state.alertVariant} onClose={() => this.setState({showAlert: false})}>
                        <Alert.Heading>{this.state.alertInfo}</Alert.Heading>
                    </Alert>
                    {!this.state.singleProduct && <h1>Checkout</h1>}
                    {this.state.singleProduct && <h1>Buying {this.state.productName} from {this.state.storeName}</h1>}
                    <Form noValidate validated={this.state.validated} className="form" onSubmit={handleSubmit}>
                        <h2>Delivery info</h2>
                        <Form.Row>
                            <Form.Group>
                                <Form.Label>Name</Form.Label>
                                <InputGroup hasValidation>
                                    <Form.Control id="name" type="text" placeholder="Enter a name" onChange={this.handleChange} required/>
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a name.
                                    </Form.Control.Feedback>
                                </InputGroup>
                            </Form.Group>
                        </Form.Row>

                        <Form.Row>
                            <Form.Group as={Col}>
                                <Form.Label>Address</Form.Label>
                                <InputGroup hasValidation>
                                    <Form.Control id="address" type="text" placeholder="Enter an address" onChange={this.handleChange} required/>
                                    <Form.Control.Feedback type="invalid">
                                        Please provide an address.
                                    </Form.Control.Feedback>
                                </InputGroup>
                            </Form.Group>
                            <Form.Group as={Col}>
                                <Form.Label>City</Form.Label>
                                <InputGroup hasValidation>
                                    <Form.Control id="city" type="text" placeholder="Enter a city" onChange={this.handleChange} required/>
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a city.
                                    </Form.Control.Feedback>
                                </InputGroup>
                            </Form.Group>
                        </Form.Row>

                        <Form.Row>
                            <Form.Group as={Col}>
                                <Form.Label>Country</Form.Label>
                                <InputGroup hasValidation>
                                    <Form.Control id="country" type="text" placeholder="Enter a country" onChange={this.handleChange} required/>
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a country.
                                    </Form.Control.Feedback>
                                </InputGroup>
                            </Form.Group>
                            <Form.Group as={Col}>
                                <Form.Label>Zip</Form.Label>
                                <InputGroup hasValidation>
                                    <Form.Control id="zip" type="text" placeholder="Enter a zip" onChange={this.handleChange} required/>
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a zip.
                                    </Form.Control.Feedback>
                                </InputGroup>
                            </Form.Group>
                        </Form.Row>

                        <h2>Payment info</h2>

                        <Form.Row>
                            <Form.Group>
                                <Form.Label>Card number</Form.Label>
                                <InputGroup hasValidation>
                                    <Form.Control id="card_number" type="text" placeholder="Enter a card number" onChange={this.handleChange} required/>
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a card number.
                                    </Form.Control.Feedback>
                                </InputGroup>
                            </Form.Group>
                        </Form.Row>

                        <Form.Row>
                            <Form.Group as={Col}>
                                <Form.Label>Expiration</Form.Label>
                                <InputGroup hasValidation>
                                    <Form.Control id="expiration" type="text" placeholder="MM/YYYY" onChange={this.handleChange} required/>
                                    <Form.Control.Feedback type="invalid">
                                        Please provide an expiration date.
                                    </Form.Control.Feedback>
                                </InputGroup>
                            </Form.Group>
                            <Form.Group as={Col}>
                                <Form.Label>CCV</Form.Label>
                                <InputGroup hasValidation>
                                    <Form.Control id="ccv" type="text" placeholder="Enter ccv" onChange={this.handleChange} required/>
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a ccv.
                                    </Form.Control.Feedback>
                                </InputGroup>
                            </Form.Group>
                        </Form.Row>

                        <Form.Row>
                            <Form.Group as={Col}>
                                <Form.Label>Card holder</Form.Label>
                                <InputGroup hasValidation>
                                    <Form.Control id="holder" type="text" placeholder="Enter card holder" onChange={this.handleChange} required/>
                                    <Form.Control.Feedback type="invalid">
                                        Please provide the card holder.
                                    </Form.Control.Feedback>
                                </InputGroup>
                            </Form.Group>
                            <Form.Group as={Col}>
                                <Form.Label>ID</Form.Label>
                                <InputGroup hasValidation>
                                    <Form.Control id="id" type="text" placeholder="Enter holder's id" onChange={this.handleChange} required/>
                                    <Form.Control.Feedback type="invalid">
                                        Please provide the holder's id.
                                    </Form.Control.Feedback>
                                </InputGroup>
                            </Form.Group>
                        </Form.Row>

                        {!this.state.submitted &&
                        <Button variant="primary" type="submit">
                            Purchase
                        </Button>}

                        {this.state.submitted &&
                        <Button variant="primary" disabled>
                            <Spinner
                                as="span"
                                animation="grow"
                                size="sm"
                                role="status"
                                aria-hidden="true"
                            />
                            Processing...
                        </Button>}
                    </Form>
                </Container>
            </div>
        );
    }
}

export default Checkout;