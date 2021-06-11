import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";
import Connection from "../API/Connection";

class StoreRevenue extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'getTotalStoreRevenue',
            managerName: window.sessionStorage.getItem('username'),
            storeId: window.sessionStorage.getItem('storeID'),
            showMessage: false,
            toShow: ''
        };
        this.handleReportResponse = this.handleReportResponse.bind(this)
    }

    onButtonClickHandler = (e) => {
        e.preventDefault();

        Connection.sendGetStoreRevenue(this.state.functionName, this.state.managerName, this.state.storeId).then(this.handleReportResponse, Connection.handleReject)

        this.setState({showMessage: true});
        //e.preventDefault();
    };

    handleReportResponse(result){
        if(!result.isFailure){
            //console.log('lalalalalla'+result.result)
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
                <h1>Store Daily Revenue Report </h1>
                <div> <label> Manager Name : <input readOnly value = {this.state.managerName} className = "managerName" type = "text" /> </label> </div>
                <div> <label> Store Id : <input className = "storeId" readOnly value = {this.state.storeId} type = "text" /> </label> </div>
                <div className="toShow"> {this.state.showMessage && <p> {this.state.toShow} </p>}
                    <button type = "button" onClick = {(e) => this.onButtonClickHandler(e)}> Show Report </button>
                </div>
            </form>
        )
    }
}

export default StoreRevenue;