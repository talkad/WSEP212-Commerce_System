import React from "react";
import Visitor from "./Visitor";
import {Link} from "react-router-dom";
import Connection from "../API/Connection";
import StaticUserInfo from "../API/StaticUserInfo";

class Registered extends React.Component{
    constructor(props) {
        super(props);

        this.handleLogout = this.handleLogout.bind(this)
        this.handleLogoutResponse = this.handleLogoutResponse.bind(this);
    }

    handleLogoutResponse(result){
        if(!result.response.isFailure){
            StaticUserInfo.setUsername(result.response.result);
            alert("logged out");
            this.props.history.push('/');
        }
        else{
            alert(result.response.errMsg);
        }
    }

    handleLogout() {
        Connection.sendLogout().then(this.handleLogoutResponse, Connection.handleReject);
        this.props.history.push('/');
    }

    render() {
        return (
            <div className="Registered">
                <p><Link onClick={this.handleLogout}>logout</Link></p>
                <p><Link to="/purchaseHistory">view your purchase history</Link></p>
                <p><Link to="/storeManagement">store management</Link></p>
                <Visitor isVisitor={false} history = {this.props.history}/>
                <br/>
                <Link to="/createStore"><button>Open your own store</button></Link>
            </div>
        );
    }
}

export default Registered;