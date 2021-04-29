import React from "react";
import Search from "../Components/Search";
import { Link } from 'react-router-dom'
import Connection from "../API/Connection";


class Visitor extends React.Component{

    constructor(props) {
        super(props);

    }

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