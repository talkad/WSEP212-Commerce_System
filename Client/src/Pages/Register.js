import React from "react";
import { Link } from 'react-router-dom'


class Register extends React.Component{

    constructor(props) {
        super(props);

        this.state = {
            username: '',
            password: '',
        }

        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
        this.goBack = this.goBack.bind(this);
    }

    handleUsernameChange(event) {
        this.setState({username: event.target.value});
    }
    handlePasswordChange(event) {
        this.setState({password: event.target.value});
    }

    handleCreateAccount(){
        //TODO: send data to server and redirect according to the result
    }

    goBack(){ //TODO: do we need a back button?
        this.props.history.goBack();
    }


    render(){
        return (
            <div className="Register">
                <h1>Create an account</h1>
                <form>
                    <input type="text" name="username" placeholder="Username" value={this.state.username}
                    onChange={this.handleUsernameChange}/>
                </form>
                <form>
                    <input type="password" name="password" placeholder="Password" value={this.state.password}
                           onChange={this.handlePasswordChange}/>
                </form>
                <div>
                    <button  onClick={this.handleCreateAccount}>create account</button>
                </div>
                <p>already a member? <Link to="/login">sign in</Link></p>
                {/*<div>*/}
                {/*    <button  onClick={this.goBack}>back</button>*/}
                {/*</div>*/}
            </div>
        );
    }
}

export default Register;