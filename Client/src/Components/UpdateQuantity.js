import React from "react";


class UpdateQuantity extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            quantity: ''
        }

        this.handleQuantityChange = this.handleQuantityChange.bind(this);
        this.handleQuantityUpdate = this.handleQuantityUpdate.bind(this);
    }

    handleQuantityChange(event){
        this.setState({quantity: event.target.value});
    }

    handleQuantityUpdate(){
        // TODO: send new quantity to server
        this.setState({quantity: ''})
        //TODO: call the handler from the parent component on success
        this.props.handler();
    }
    render() {
        return (
            <div>
                <form>
                    <input type="text" name="quantity" placeholder="New quantity" value={this.state.quantity}
                              onChange={this.handleQuantityChange}/>
                    <button onClick={this.handleQuantityUpdate}>Update quantity</button>
                </form>
            </div>
        );
    }
}

export default UpdateQuantity;