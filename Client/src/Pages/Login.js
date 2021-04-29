import React from "react";
import Connection from "../API/Connection";
import StaticUserInfo from "../API/StaticUserInfo";


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
                <h1>Hello there</h1>
                <form>
                    <input type="text" name="username" placeholder="Username" value={this.state.username}
                           onChange={this.handleUsernameChange}/>
                </form>
                <form>
                    <input type="password" name="password" placeholder="Password" value={this.state.password}
                           onChange={this.handlePasswordChange}/>
                </form>
                <div>
                    <button  onClick={this.handleSignIn}>Sign in</button>
                </div>
            </div>
        );
    }
}

export default Login;