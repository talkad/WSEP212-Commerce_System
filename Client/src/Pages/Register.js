import React from "react";
import { Link } from 'react-router-dom'
import Connection from "../API/Connection";
import {Alert, Button, Container, Form, InputGroup, Spinner} from "react-bootstrap";


class Register extends React.Component{

    constructor(props) {
        super(props);

        this.state = {
            username: '',
            password: '',
            submitted: false,
            validated: false,

            showAlert: false,
            alertVariant: '',
            alertInfo: '',
        }

        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
        this.handleCreateAccount = this.handleCreateAccount.bind(this);
        this.handleResponse = this.handleResponse.bind(this);
    }

    handleUsernameChange(event) {
        this.setState({username: event.target.value});
    }
    handlePasswordChange(event) {
        this.setState({password: event.target.value});
    }

    handleResponse(result){
        if(!result.response.isFailure){
            this.setState({submitted: false,
                showAlert: true, alertVariant: 'success', alertInfo: 'registered successfully'});
            document.location.href = "/";
        }
        else{
            this.setState({username: '', password: '', submitted: false,
                showAlert: true, alertVariant: 'danger', alertInfo: result.response.errMsg});
        }
    }

    handleCreateAccount(){
        this.setState({submitted: true});
        Connection.sendRegister(this.state.username, this.state.password).then(this.handleResponse, Connection.handleReject);
    }

    render(){
        const handleSubmit = (event) => {
            const form = event.currentTarget;
            event.preventDefault();
            if (form.checkValidity() === false) {
                event.stopPropagation();
            }

            this.setState({validated: true})
            this.handleCreateAccount();
        };

        return (
            <div className="Login">
                <Container className="Page">
                    <Alert show={this.state.showAlert} variant={this.state.alertVariant} onClose={() => this.setState({showAlert: false})}>
                        <Alert.Heading>{this.state.alertInfo}</Alert.Heading>
                    </Alert>
                    <h1>Create an account</h1>
                    <Form noValidate validated={this.state.validated} className="form" onSubmit={handleSubmit}>
                        <div className="textStyle">
                            <Form.Group controlId="formBasicEmail">
                                <Form.Label>Username:</Form.Label>
                                <InputGroup hasValidation>
                                    <Form.Control type="text" placeholder="Enter username" value={this.state.username}
                                                  onChange={this.handleUsernameChange} required/>
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a username.
                                    </Form.Control.Feedback>
                                </InputGroup>
                            </Form.Group>
                        </div>
                        <div className="textStyle">
                            <Form.Group controlId="formBasicPassword">
                                <Form.Label>Password:</Form.Label>
                                <InputGroup hasValidation>
                                    <Form.Control type="password" placeholder="Enter password" value={this.state.password}
                                                  onChange={this.handlePasswordChange} required/>
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a password.
                                    </Form.Control.Feedback>
                                </InputGroup>
                            </Form.Group>
                        </div>
                        <div className="buttonStyle">
                            {!this.state.submitted &&
                            <Button variant="primary" type="submit">
                                Create account
                            </Button>}

                            {this.state.submitted &&
                            <Button variant="primary" disabled>
                                <Spinner animation="border" size="sm"/>
                            </Button>}
                        </div>
                    </Form>
                    <div className="divStyle">
                        <p>already a member? <Link to="/login">log in</Link></p>
                    </div>
                </Container>
            </div>
        );
    }
}

export default Register;