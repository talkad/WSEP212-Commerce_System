import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";
import Connection from "../API/Connection";

class DiscountsDetails extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'getDiscountPolicy',
            username: window.sessionStorage.getItem('username'),
            storeId: window.sessionStorage.getItem('storeID'),
            showMessage: false,
            toShow: ''
        };
        this.handleReportResponse = this.handleReportResponse.bind(this)
    }

    onButtonClickHandler = (e) => {
        e.preventDefault();

        Connection.sendDiscountPurchaseReportRequest(this.state.functionName, this.state.username, this.state.storeId).then(this.handleReportResponse, Connection.handleReject)

        this.setState({showMessage: true});
    };

    handleReportResponse(result){
        if(!result.isFailure){
            // let show = "";
            // let DiscountCounter = 1;
            //
            // result.result.forEach(element => show = show.concat(
            //     "Discount Num: " + DiscountCounter++ + " Num Of Rules: " +
            //     result.result[0].size().toString()));

            this.setState({toShow: result.result});
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
                <h1>Discount Details Page </h1>
                <div> <label> Username Name : <input readOnly value = {this.state.username} className = "username" type = "text" onChange = {(e) => this.handleInputChange(e, 'username')}/> </label> </div>
                <div> <label> Store Id : <input readOnly value = {this.state.storeId} className = "storeId" type = "text" onChange = {(e) => this.handleInputChange(e, 'storeId')}/> </label> </div>
                <div className="toShow"> {this.state.showMessage && <p> {this.state.toShow} </p>}
                    <button type = "button" onClick = {(e) => this.onButtonClickHandler(e)}> Show Details </button>
                </div>
            </form>
        )
    }
}

export default DiscountsDetails;