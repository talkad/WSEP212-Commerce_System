import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";
import Connection from "../API/Connection";

class StorePurchaseHistory extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'getStorePurchaseHistory',
            adminName: StaticUserInfo.getUsername(),
            storeId: StaticUserInfo.getStoreId(),
            showMessage: false,
            toShow: ''
        };
        this.handleReportResponse = this.handleReportResponse.bind(this)
    }

    onButtonClickHandler = (e) => {
        e.preventDefault();

        Connection.sendReportRequest(this.state.functionName, this.state.adminName, this.state.storeId).then(this.handleReportResponse, Connection.handleReject)

        this.setState({showMessage: true});
        //e.preventDefault();
    };

    handleReportResponse(result){
        if(!result.isFailure){
            this.setState({toShow: result.result.toString()})
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
                <h1>Purchase History Details For Store Page </h1>
                <div> <label> Admin Name : <input readOnly value = {this.state.adminName} className = "adminName" type = "text" onChange = {(e) => this.handleInputChange(e, 'adminName')}/> </label> </div>
                <div> <label> Store Id : <input className = "storeId" type = "text" onChange = {(e) => this.handleInputChange(e, 'StoreId')}/> </label> </div>
                <div className="toShow"> {this.state.showMessage && <p> {this.state.toShow} </p>}
                    <button type = "button" onClick = {(e) => this.onButtonClickHandler(e)}> Show History </button>
                </div>
            </form>
        )
    }
}

export default StorePurchaseHistory;