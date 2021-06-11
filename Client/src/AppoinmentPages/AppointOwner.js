import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";
import Connection from "../API/Connection";

class AppointOwner extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'appointStoreOwner',
            appointerName: window.sessionStorage.getItem('username'),
            appointeeName: '',
            storeId: window.sessionStorage.getItem('storeID'),
        };
    }

    handleClick(e) {
        e.preventDefault();
        Connection.sendAppoints(this.state.functionName, this.state.appointerName, this.state.appointeeName, this.state.storeId).then(this.handleAppointResponse, Connection.handleReject)
    }

    handleAppointResponse(result){
        if(!result.isFailure){
            alert("appointing successful");
        }
        else{
            alert(result.errMsg);
        }
    }

    handleInputChange = (e, name) => {
        this.setState({
            [name]: e.target.value
        })
    }

    render() {
        return (
            <form>
                <h1>Appoint Owner Page </h1>
                <div> <label> Appointer Name : <input readOnly value = {this.state.appointerName} className = "appointerName" type = "text" onChange = {(e) => this.handleInputChange(e, 'appointerName')}/> </label> </div>
                <div> <label> Appointee Name : <input className = "appointeeName" type = "text" onChange = {(e) => this.handleInputChange(e, 'appointeeName')}/> </label> </div>
                <div> <label> Store Id : <input readOnly value = {this.state.storeId} className = "storeId" type = "text" onChange = {(e) => this.handleInputChange(e, 'storeId')}/> </label> </div>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Appoint Owner </button>
            </form>
        )
    }
}

export default AppointOwner;