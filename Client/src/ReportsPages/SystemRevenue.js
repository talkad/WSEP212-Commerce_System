import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";
import Connection from "../API/Connection";
import stonks from '../Images/stonks.jpg'
import {Image, Spinner} from "react-bootstrap";

class SystemRevenue extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'getTotalSystemRevenue',
            adminName: window.sessionStorage.getItem('username'),
            showMessage: false,
            toShow: '',
            loaded: false
        };
        this.handleReportResponse = this.handleReportResponse.bind(this)
    }

    componentDidMount() {
        Connection.sendGetSystemRevenue(this.state.functionName, this.state.adminName).then(this.handleReportResponse, Connection.handleReject)
    }

    onButtonClickHandler = (e) => {
        e.preventDefault();

        Connection.sendGetSystemRevenue(this.state.functionName, this.state.adminName).then(this.handleReportResponse, Connection.handleReject)

        this.setState({showMessage: true});
        //e.preventDefault();
    };

    handleReportResponse(result){
        if(!result.isFailure){
            this.setState({toShow: result.result.toString(), showMessage: false, loaded: true})
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
            <div>
                <h1>System Daily Revenue Report </h1>
                {/*<div> <label> Admin Name : <input value = {this.state.adminName} className = "adminName" type = "text" onChange = {(e) => this.handleInputChange(e, 'adminName')}/> </label> </div>*/}
                {/*<div className="toShow"> {this.state.showMessage && <p> {this.state.toShow} </p>}*/}
                {/*    /!*<button type = "button" onClick = {(e) => this.onButtonClickHandler(e)}> Show Report </button>*!/*/}
                {/*</div>*/}
                {!this.state.loaded && <Spinner animation="grow"/>}
                {this.state.loaded && <div class="image">
                    <Image src={stonks} alt="" />
                    <h2 className="stonks"><span>{this.state.toShow}</span></h2><br/>
                </div>}

                {/*<div className="container">*/}
                {/*     <Image src={stonks} alt="Snow"/>*/}
                {/*         <div className="top-left">Top Left</div>*/}
                {/*</div>*/}
            </div>
        )
    }
}

export default SystemRevenue;

