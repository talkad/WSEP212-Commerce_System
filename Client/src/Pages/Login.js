import React from "react";
import Connection from "../API/Connection";
import StaticUserInfo from "../API/StaticUserInfo";
import {Form, Button, Container, Spinner} from 'react-bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import {Link} from "react-router-dom";

class Login extends React.Component{

    constructor(props) {
        super(props);

        this.state = {
            username: '',
            password: '',
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
        if(!result.response.isFailure){
            StaticUserInfo.setUsername(result.response.result);
            this.props.history.push('/registered');
        }
        else{
            alert(result.response.errMsg);
            this.setState({username: '', password: ''});
        }
    }

    handleSignIn(){
        Connection.sendLogin(this.state.username, this.state.password).then(this.handleResponse, Connection.handleReject);
    }

    render(){
        return (
            <div className="Login">

                <Container className="Page">
                    <h1>Login</h1>

                    <Form className="form">
                        <div className="divStyle">
                            <Form.Group controlId="formBasicEmail">
                                <Form.Label>Username:</Form.Label>
                                <Form.Control type="text" placeholder="Enter username" value={this.state.username}
                                              onChange={this.handleUsernameChange}/>
                            </Form.Group>
                         </div>

                        <div className="divStyle">
                            <Form.Group controlId="formBasicPassword">
                                <Form.Label>Password:</Form.Label>
                                <Form.Control type="password" placeholder="Enter password" value={this.state.password}
                                              onChange={this.handlePasswordChange}/>
                            </Form.Group>
                        </div>

                    </Form>

                    <div className="divStyle">
                        <Button variant="primary" type="submit" onClick={this.handleSignIn}>
                            Submit
                        </Button>

                        <Button variant="primary" disabled>
                            <Spinner
                                as="span"
                                animation="grow"
                                size="sm"
                                role="status"
                                aria-hidden="true"
                            />
                            Loading...
                        </Button>
                    </div>

                    <div className="divStyle">
                        <p>not a member yet? <Link to="/register">register</Link></p>
                    </div>
                </Container>

                {/*<form>*/}
                {/*    <input type="text" name="username" placeholder="Username" value={this.state.username}*/}
                {/*           onChange={this.handleUsernameChange}/>*/}
                {/*</form>*/}
                {/*<form>*/}
                {/*    <input type="password" name="password" placeholder="Password" value={this.state.password}*/}
                {/*           onChange={this.handlePasswordChange}/>*/}
                {/*</form>*/}
                {/*<div>*/}
                {/*    <button  onClick={this.handleSignIn}>Sign in</button>*/}
                {/*</div>*/}
            </div>
        );
    }
}

export default Login;