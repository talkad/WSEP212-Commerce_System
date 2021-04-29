import React from "react";
import Search from "../Components/Search";
import { Link } from 'react-router-dom'
import Connection from "../API/Connection";

// const client = new WebSocket('ws://localhost:8080');
// client.onopen = () => {
//     console.log("connected");
// }
// import {io} from "socket.io-client";
//
// const socket = io("ws://localhost:8080");
//
// socket.on("connect", () => {
//     // either with send()
//     socket.send("Hello!");
//
//     // or with emit() and custom event names
//     socket.emit("salutations", "Hello!", { "mr": "john" }, Uint8Array.from([1, 2, 3, 4]));
// });
//
// // handle the event sent with socket.send()
// socket.on("message", data => {
//     console.log(data);
// });
//
// // handle the event sent with socket.emit()
// socket.on("greetings", (elem1, elem2, elem3) => {
//     console.log(elem1, elem2, elem3);
// });

class Visitor extends React.Component{

    constructor(props) {
        super(props);

    }

    // componentDidMount() {
    //     client.onopen = () => {
    //         console.log("connected");
    //         //client.send("hello");
    //     }
    //
    //     client.close = () => {
    //         console.log("disconnected");
    //     }
    //
    //     client.onmessage = (message) => {
    //         console.log(message);
    //     }
    // }

    render() {
        return(
            <div className="Visitor">
                {this.props.isVisitor &&
                <p><Link to="/login">Sign in</Link> or <Link to="/register">register</Link></p>}
                <Link to="/cart">Cart</Link>
                <h1>welcome to the commerce system</h1>
                <Search/>
            </div>
        );
    }
}

export default Visitor;