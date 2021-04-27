import React from "react";


class CreateStore extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            storeName: ''
        }

        this.handleStoreNameChange = this.handleStoreNameChange.bind(this);
        this.handleCreateStore = this.handleCreateStore.bind(this);
    }

    handleStoreNameChange(event) {
        this.setState({storeName: event.target.value})
    }

    handleCreateStore(){
        // TODO: send to server the create store data
        // TODO: send the user to a main page destined for store owners
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
