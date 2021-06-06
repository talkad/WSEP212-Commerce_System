import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";
import Connection from "../API/Connection";

class SystemRevenue extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'getTotalSystemRevenue',
            adminName: StaticUserInfo.getUsername(),
            showMessage: false,
            toShow: ''
        };
        this.handleReportResponse = this.handleReportResponse.bind(this)
    }

    onButtonClickHandler = (e) => {
        e.preventDefault();

        Connection.sendGetSystemRevenue(this.state.functionName, this.state.adminName).then(this.handleReportResponse, Connection.handleReject)

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
                <h1>System Daily Revenue Report </h1>
                <div> <label> Admin Name : <input readOnly value = {this.state.adminName} className = "adminName" type = "text"/> </label> </div>
                <div className="toShow"> {this.state.showMessage && <p> {this.state.toShow} </p>}
                    <button type = "button" onClick = {(e) => this.onButtonClickHandler(e)}> Show Report </button>
                </div>
            </form>
        )
    }
}

export default SystemRevenue;