import React from "react";
import Search from "../Components/Search";
import { Link } from 'react-router-dom'


class Visitor extends React.Component{


    render() {
        return(
            <div className="Visitor">
                <p><Link to="/login">Sign in</Link> or <Link to="/register">register</Link></p>
                <h1>welcome to the commerce system</h1>
                <Search/>
            </div>
        );
    }
}

export default Visitor;