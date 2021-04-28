import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";

class UserPurchaseHistory extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'getUserPurchaseHistory',
            adminName: StaticUserInfo.getUsername(),
            userName: '',
            showMessage: false,
            toShow: 'ghi' //TODO: this f** newline doesnt workkkkk
        };
    }
    
    onButtonClickHandler = (e) => {
        this.setState({showMessage: true});
        e.preventDefault();
    };

    handleInputChange = (e, name) => {
        this.setState({
            [name]: e.target.value
        })
    }

    render(){
        return(
            <form>
                <h1>Purchase History Details For User Page </h1>
                <div> <label> Admin Name : <input readOnly value = {this.state.adminName} className = "adminName" type = "text" onChange = {(e) => this.handleInputChange(e, 'adminName')}/> </label> </div>
                <div> <label> User Name : <input className = "username" type = "text" onChange = {(e) => this.handleInputChange(e, 'username')}/> </label> </div>
                <div className="toShow"> {this.state.showMessage && <p> {this.state.toShow} </p>}
                <button type = "button" onClick = {(e) => this.onButtonClickHandler(e)}> Show History </button>
                </div>
            </form>
        )
    }
}

export default UserPurchaseHistory;