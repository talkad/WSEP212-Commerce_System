import Search from "../Components/Search";
import React from "react";


class Home extends React.Component{
    constructor(props) {
        super(props);

    }

    render() {
        return(
            <div>
                <h1>welcome to the very cool commerce system</h1>
                <Search/>
            </div>
        );
    }
}

export default Home;