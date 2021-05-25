import React from "react";
import {Button, Card, Form, InputGroup, Spinner} from "react-bootstrap";
import Connection from "../API/Connection";
import Modal from 'react-modal'
import * as Icon from 'react-bootstrap-icons';

class ProductEntry extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            priceOfferModalShow: false,
            priceOffer: '',
            validated: false,
        }

        this.handleAddToCartResponse = this.handleAddToCartResponse.bind(this);
        this.openPriceOfferModal = this.openPriceOfferModal.bind(this);
        this.closePriceOfferModal = this.closePriceOfferModal.bind(this);
        this.handleAddToCart = this.handleAddToCart.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleOfferPriceResponse = this.handleOfferPriceResponse.bind(this);
    }

    handleAddToCartResponse(result) {
        if (!result.isFailure) {
            alert("product added successfully to cart");
        } else {
            alert(result.errMsg);
        }
    }

    handleAddToCart() {
        Connection.sendAddToCart(this.props.storeID, this.props.productID).then(this.handleAddToCartResponse, Connection.handleReject);
    }

    handleOfferPriceResponse(result) {
        if (!result.isFailure) {
            alert("price offer sent!");
            this.setState({priceOfferModalShow: false, priceOffer: '', submitted: false});
        } else {
            alert(result.errMsg);
        }
    }

    handleOfferPrice() {
        if(this.state.priceOffer !== ''){
            Connection.sendOfferPrice(this.props.productID, this.props.storeID, this.state.priceOffer).then(this.handleOfferPriceResponse, Connection.handleReject);
            this.setState({ submitted: true });
        }
    }

    openPriceOfferModal(){
        this.setState({ priceOfferModalShow: true })
    }

     closePriceOfferModal(){
         this.setState({ priceOfferModalShow: false })
     }

    handleChange(event) {
        this.setState({[event.target.id]: event.target.value})
    }

    render() {
        const handleSubmit = (event) => {
            const form = event.currentTarget;
            event.preventDefault();
            if (form.checkValidity() === false) {
                event.stopPropagation();
            }

            this.setState({validated: true})
            this.handleOfferPrice();
        };

        return(
          <div className='search-result-div'>
              <Card border='dark' className='card'>
                 <Card.Body>
                     <Card.Title>{this.props.name}</Card.Title>
                     <Card.Subtitle>price: {this.props.price} ₪</Card.Subtitle>
                     <Card.Text>Sold by: {this.props.storeID}</Card.Text>
                     {this.props.reviews.length !== 0 && <Card.Subtitle>Reviews:</Card.Subtitle>}
                     <Card.Text>{this.props.reviews.map(({username, review}) => username + ": " + review + "\n")}</Card.Text>
                     <Button variant="primary" className='card-button' size="sm"
                             onClick={this.handleAddToCart}><span style={{fontSize: "small"}}>Add to cart</span></Button>
                     <br/>
                     <Button variant="primary" className='card-button' size="sm"
                             onClick={this.openPriceOfferModal}><span style={{fontSize: "small"}}>Offer price</span></Button>
                 </Card.Body>
              </Card>

              <Modal
                  style={{
                      overlay: {

                          position: 'absolute',
                          top: '50%',
                          left: '50%',
                          transform: 'translate(-50%, -50%)',
                          backgroundColor: 'rgba(255, 255, 255, 0.75)'
                      },
                      // content: {
                      //     position: 'absolute',
                      //     top: '40px',
                      //     left: '40px',
                      //     right: '40px',
                      //     bottom: '40px',
                      //     border: '1px solid #ccc',
                      //     background: '#fff',
                      //     overflow: 'auto',
                      //     WebkitOverflowScrolling: 'touch',
                      //     borderRadius: '4px',
                      //     outline: 'none',
                      //     padding: '20px'
                      // }
                  }}

                  isOpen={this.state.priceOfferModalShow}
                  onRequestClose={this.closePriceOfferModal}
                  contentLabel="Example Modal"
              >
                  <Icon.XCircle onClick={this.closePriceOfferModal}/>
                  <br/>
                  <h1>Offer Price</h1>
                  <br/>
                  <Form noValidate validated={this.state.validated} className="form" onSubmit={handleSubmit}>
                      <Form.Row>
                          <Form.Group>
                              <Form.Label>Offer</Form.Label>
                              <InputGroup hasValidation>
                                  <Form.Control id="priceOffer" type="text" placeholder="Enter a price offer" onChange={this.handleChange} required/>
                                  <Form.Control.Feedback type="invalid">
                                      Please provide an offer.
                                  </Form.Control.Feedback>
                              </InputGroup>
                          </Form.Group>
                      </Form.Row>
                      {!this.state.submitted &&
                      <Button variant="primary" type="submit">
                          Offer
                      </Button>}

                      {this.state.submitted &&
                      <Button variant="primary" disabled>
                          <Spinner
                              as="span"
                              animation="border"
                              size="sm"
                              role="status"
                              aria-hidden="true"
                          />
                           Sending offer...
                      </Button>}
                  </Form>
              </Modal>
          </div>
        );
    }
}

export default ProductEntry;