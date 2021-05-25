import {Image} from "react-bootstrap";
import React from "react";
import difficulties from './Images/difficulties.png'

class Disconnected extends React.Component{
    constructor(props) {
        super(props);

    }

    render() {
        return (
            <div>
                <img src={difficulties}/>
            </div>
        );
    }

}

export default Disconnected;