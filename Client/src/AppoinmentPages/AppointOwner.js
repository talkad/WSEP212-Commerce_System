import React from "react";
import StaticUserInfo from "../API/StaticUserInfo";

class AppointOwner extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            functionName: 'appointStoreOwner',
            appointerName: StaticUserInfo.getUsername(),
            appointeeName: '',
            storeId: StaticUserInfo.getStoreId(),
        };
    }

    handleClick(e) {
        // access input values in the state
        console.log(this.state)
        e.preventDefault();
    }

    handleInputChange = (e, name) => {
        this.setState({
            [name]: e.target.value
        })
    }

    render() {
        return (
            <form>
                <h1>Appoint Owner Page </h1>
                <div> <label> Appointer Name : <input readOnly value = {this.state.appointerName} className = "appointerName" type = "text" onChange = {(e) => this.handleInputChange(e, 'appointerName')}/> </label> </div>
                <div> <label> Appointee Name : <input className = "appointeeName" type = "text" onChange = {(e) => this.handleInputChange(e, 'appointeeName')}/> </label> </div>
                <div> <label> Store Id : <input readOnly value = {this.state.storeId} className = "storeId" type = "text" onChange = {(e) => this.handleInputChange(e, 'storeId')}/> </label> </div>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Appoint Owner </button>
            </form>
        )
    }
}

export default AppointOwner;