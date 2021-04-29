import React from "react";

class StoreEntry extends React.Component{

    render() {
        return (
            <div>
                <h2>{this.props.name}</h2>
                <h3>id: {this.props.id}</h3>
            </div>
        );
    }
}

export default StoreEntry;