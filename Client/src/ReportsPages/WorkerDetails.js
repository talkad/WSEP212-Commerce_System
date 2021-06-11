import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";
import Connection from "../API/Connection";

class WorkerDetails extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'getStoreWorkersDetails',
            username: window.sessionStorage.getItem('username'),
            storeId: window.sessionStorage.getItem('storeID')(),
            showMessage: false,
            toShow: '',
        };
        this.handleReportResponse = this.handleReportResponse.bind(this)
    }

    onButtonClickHandler = (e) => {
        e.preventDefault();

        Connection.sendGetStoreWorkersInfo(this.state.functionName, this.state.username, this.state.storeId).then(this.handleReportResponse, Connection.handleReject)

        this.setState({showMessage: true});
    };

    handleReportResponse(result){
        if(!result.isFailure){
            let show = "";

            result.result.forEach(element => show = show.concat(
                "Employee's name is " + element.name.toString() + ", he ownes " + element.storesOwned.length.toString() + " stores and managing " + element.storesManaged.length.toString() + " stores :::  "));

            this.setState({toShow: show});
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

    render(){
        return(
            <form>
                <h1>Show Worker Details Page </h1>
                <div> <label> UserName : <input readOnly value = {this.state.username} className = "username" type = "text" /> </label> </div>
                <div> <label> Store Id : <input readOnly value = {this.state.storeId} className = "storeId" type = "text" /> </label> </div>
                <div className="toShow"> {this.state.showMessage && <p> {this.state.toShow} </p>}
                    <button type = "button" onClick = {(e) => this.onButtonClickHandler(e)}> Show Report </button>
                </div>
            </form>
        )
    }
}

export default WorkerDetails;