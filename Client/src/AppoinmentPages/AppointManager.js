import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";
import Connection from "../API/Connection";

class AppointManager extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'appointStoreManager',
            appointerName: StaticUserInfo.getUsername(), //username
            appointeeName: '',
            storeId: StaticUserInfo.getStoreId(),
        };
    }

    handleClick(e) {
        e.preventDefault();
        Connection.sendAppoints(this.state.functionName, this.state.appointerName, this.state.appointeeName, this.state.storeId).then(this.handleAppointResponse, Connection.handleReject())
    }

    handleAppointResponse(result){
        if(!result.response.isFailure){
            alert("appointing successful");
        }
        else{
            alert(result.response.errMsg);
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
                <h1>Appoint Manager Page </h1>
                <div> <label> Appointer Name : <input readOnly value = {this.state.appointerName} className = "appointerName" type = "text" onChange = {(e) => this.handleInputChange(e, 'appointerName')}/> </label> </div>
                <div> <label> Appointee Name : <input className = "appointeeName" type = "text" onChange = {(e) => this.handleInputChange(e, 'appointeeName')}/> </label> </div>
                <div> <label> Store Id : <input readOnly value = {this.state.storeId} className = "storeId" type = "text" onChange = {(e) => this.handleInputChange(e, 'storeId')}/> </label> </div>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Appoint Manager </button>
            </form>
        )
    }
}

export default AppointManager;