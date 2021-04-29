import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";
import Connection from "../API/Connection";

class WorkerDetails extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'getStoreWorkersDetails',
            username: StaticUserInfo.getUsername(),
            storeId: StaticUserInfo.getStoreId(),
            showMessage: false,
            toShow: 'abcdef' //TODO: this f** newline doesnt workkkkk
        };
    }
    onButtonClickHandler = (e) => {
        e.preventDefault();

        Connection.sendReportRequest(this.state.functionName, this.state.adminName, this.state.storeId).then(this.handleReportResponse, Connection.handleReject())

        this.setState({showMessage: true});
        //e.preventDefault();
    };

    handleReportResponse(result){
        if(!result.response.isFailure){
            this.setState({toShow: result.response.result.toString()})//TODO check
        }
        else{
            alert(result.response.errMsg);
        }
    }

    render(){
        return(
            <form>
                <h1>Show Worker Details Page </h1>
                <div> <label> UserName : <input readOnly value = {this.state.username} className = "username" type = "text" onChange = {(e) => this.handleInputChange(e, 'username')}/> </label> </div>
                <div> <label> Store Id : <input readOnly value = {this.state.storeId} className = "storeId" type = "text" onChange = {(e) => this.handleInputChange(e, 'storeId')}/> </label> </div>
                <div className="toShow"> {this.state.showMessage && <p> {this.state.toShow} </p>}
                <button type = "button" onClick = {(e) => this.onButtonClickHandler(e)}> Show Report </button>
                </div>
            </form>
        )
    }
}

export default WorkerDetails;
// <button onClick={this.onButtonClickHandler()}>Show Report</button>