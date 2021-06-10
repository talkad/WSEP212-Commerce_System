import React, {useState} from "react";
import Connection from "../API/Connection";
import StaticUserInfo from "../API/StaticUserInfo";
import {Form, Button, Container, Spinner, InputGroup, Alert, Image} from 'react-bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import {Link} from "react-router-dom";
import hello_there from "../Images/harold_star_wars.gif";

class Login extends React.Component{

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
        this.handleSignIn = this.handleSignIn.bind(this);
        this.handleResponse = this.handleResponse.bind(this);
    }

    handleUsernameChange(event) {
        this.setState({username: event.target.value});
    }
    handlePasswordChange(event) {
        this.setState({password: event.target.value});
    }

    handleResponse(result) {
        console.log("hello there");
        if(!result.isFailure){
            console.log("general kenobi");
            window.sessionStorage.setItem('username', result.result);
            this.setState({submitted: false,
                showAlert: true, alertVariant: 'success', alertInfo: 'Logged in!'});
            document.location.href = "/";
        }
        else{
            console.log("you're a bold one");
            this.setState({username: '', password: '', submitted: false,
            showAlert: true, alertVariant: 'danger', alertInfo: result.errMsg});
        }
    }

    handleSignIn(){
        if(this.state.username !== '' && this.state.password !== ''){
            this.setState({submitted: true});
            Connection.sendLogin(this.state.username, this.state.password).then(this.handleResponse, Connection.handleReject);
        }
    }

    render(){
        const handleSubmit = (event) => {
            const form = event.currentTarget;
            event.preventDefault();
            if (form.checkValidity() === false) {
                event.stopPropagation();
            }

            this.setState({validated: true})
            this.handleSignIn();
        };

        return (
            <div className="Login">
                <Container className="Page">
                    <Alert show={this.state.showAlert} variant={this.state.alertVariant} onClose={() => this.setState({showAlert: false})}>
                        <Alert.Heading>{this.state.alertInfo}</Alert.Heading>
                    </Alert>
                    <h1>Login</h1>
                    <Image src={hello_there} fluid/>
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
                                Log in
                            </Button>}

                            {this.state.submitted &&
                            <Button variant="primary" disabled>
                                <Spinner animation="border" size="sm"/>
                            </Button>}
                        </div>
                    </Form>
                    <div className="divStyle">
                        <p>not a member yet? <Link to="/register">register</Link></p>
                    </div>
                </Container>
            </div>
        );
    }
}

export default Login;