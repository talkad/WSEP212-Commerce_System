import React from "react";
import Connection from "../API/Connection";
import StaticUserInfo from "../API/StaticUserInfo";
import {Alert, Button, Container, Form, Image, InputGroup, Spinner} from "react-bootstrap";
import {Link} from "react-router-dom";


class CreateStore extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            storeName: '',
            submitted: false,
            validated: false,

            showAlert: false,
            alertVariant: '',
            alertInfo: '',
        }

        this.handleStoreNameChange = this.handleStoreNameChange.bind(this);
        this.handleCreateStore = this.handleCreateStore.bind(this);
        this.handleResponse = this.handleResponse.bind(this);
        this.handleChange = this.handleChange.bind(this);
    }

    handleStoreNameChange(event) {
        this.setState({storeName: event.target.value})
    }

    handleChange(event) {
        this.setState({[event.target.id]: event.target.value})
    }

    handleResponse(result){
        if(!result.isFailure){
            this.setState({submitted: false,
                showAlert: true, alertVariant: 'success', alertInfo: 'Store Created!'});
            document.location.href = "/";
        }
        else{
            this.setState({storeName: '', submitted: false,
                showAlert: true, alertVariant: 'danger', alertInfo: result.errMsg});
        }
    }

    handleCreateStore(){
        Connection.sendOpenStore(this.state.storeName).then(this.handleResponse, Connection.handleReject);
        this.setState({submitted: true});
    }

    render() {
        const handleSubmit = (event) => {
            const form = event.currentTarget;
            event.preventDefault();
            if (form.checkValidity() === false) {
                event.stopPropagation();
            }

            this.setState({validated: true})
            this.handleCreateStore();
        };

        return (
            <div>
                <Container className="Page">
                    <Alert show={this.state.showAlert} variant={this.state.alertVariant} onClose={() => this.setState({showAlert: false})}>
                        <Alert.Heading>{this.state.alertInfo}</Alert.Heading>
                    </Alert>
                    <h1>Create your own store</h1>
                    <Image src="https://thumbs.dreamstime.com/b/open-opening-store-shop-young-people-holding-banner-isolated-59691070.jpg"/>
                    <Form noValidate validated={this.state.validated} className="form" onSubmit={handleSubmit}>
                        <div className="textStyle">
                            <Form.Group>
                                <Form.Label>Store name:</Form.Label>
                                <InputGroup hasValidation>
                                    <Form.Control id="storeName"  type="text" placeholder="Enter store name"
                                                  onChange={this.handleChange} required/>
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a store name.
                                    </Form.Control.Feedback>
                                </InputGroup>
                            </Form.Group>
                        </div>
                        <div className="buttonStyle">
                            {!this.state.submitted &&
                            <Button variant="primary" type="submit">
                                Create Store!
                            </Button>}

                            {this.state.submitted &&
                            <Button variant="primary" disabled>
                                <Spinner animation="border" size="sm"/>
                            </Button>}
                        </div>
                    </Form>
                </Container>


                {/*<form>*/}
                {/*    <input type="text" placeholder="Store name" value={this.state.storeName}*/}
                {/*    onChange={this.handleStoreNameChange}/>*/}
                {/*</form>*/}
                {/*<button onClick={this.handleCreateStore}>Create Store!</button>*/}
            </div>
        );
    }
}

export default CreateStore;
