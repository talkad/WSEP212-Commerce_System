import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";
import {Link} from "react-router-dom";

class ChooseMyStore extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            storeId: '',
        };
    }

    handleClick(e) {
        // access input values in the state
        //console.log(this.state.storeId)
        StaticUserInfo.setStoreId(this.state.storeId)
        e.preventDefault();
        alert('Store Id chosen');
    }

    handleInputChange = (e, name) => {
        this.setState({
            [name]: e.target.value
        })
    }

    render() {
        return (
            <form>
                <h1>Choose your store </h1>
                <div> <label> Store Id : <input className = "amount" type = "text" onChange = {(e) => this.handleInputChange(e, 'storeId')}/> </label> </div>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Choose Store </button>
                <p><Link to="/storeManagement">store management</Link></p>
            </form>
        )
    }
}

export default ChooseMyStore;