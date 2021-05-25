import React from "react";
import {Image} from "react-bootstrap";
import difficulties from '../Images/difficulties.png'

class Disconnected extends React.Component{
    constructor(props) {
        super(props);

    }

    render() {
        return (
            <div>
                <Image src={difficulties}/>
            </div>
        );
    }

}

export default Disconnected;