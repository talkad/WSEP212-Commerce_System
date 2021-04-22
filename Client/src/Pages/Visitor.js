import React from "react";
import { Link } from 'react-router-dom'
import Login from './Login'


class Visitor extends React.Component{

    render() {
        return(
            <div className="Visitor">
                <h1>welcome to the commerce system</h1>
                <Link to="/login">
                    <button>login</button>
                </Link>
                <Link to="/register">
                    <button>register</button>
                </Link>
            </div>
        );
    }
}

export default Visitor;