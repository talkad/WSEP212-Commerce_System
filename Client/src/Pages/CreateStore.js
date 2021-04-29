import React from "react";
import Connection from "../API/Connection";
import StaticUserInfo from "../API/StaticUserInfo";


class CreateStore extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            storeName: ''
        }

        this.handleStoreNameChange = this.handleStoreNameChange.bind(this);
        this.handleCreateStore = this.handleCreateStore.bind(this);
        this.handleResponse = this.handleResponse.bind(this);
    }

    handleStoreNameChange(event) {
        this.setState({storeName: event.target.value})
    }

    handleResponse(result){
        if(!result.response.isFailure){
            StaticUserInfo.setStoreId(result.response.result);
            alert("Store created successfully");
            this.props.history.goBack();
        }
        else{
            alert(result.response.errMsg);
        }
    }

    handleCreateStore(){
        Connection.sendOpenStore(this.state.storeName).then(this.handleResponse, Connection.handleReject);
    }

    render() {
        return (
            <div>
                <h1>Create your own store</h1>
                <form>
                    <input type="text" placeholder="Store name" value={this.state.storeName}
                    onChange={this.handleStoreNameChange}/>
                </form>
                <button onClick={this.handleCreateStore}>Create Store!</button>
            </div>
        );
    }
}

export default CreateStore;
