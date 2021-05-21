import Search from "../Components/Search";
import React from "react";
import {Container, Image} from "react-bootstrap";


class Home extends React.Component{
    constructor(props) {
        super(props);

    }

    render() {
        return(
            <div>
                <h1>welcome to the very cool commerce system</h1>
                <Image src="https://pbs.twimg.com/media/DNjuJMNVoAAAWLy.jpg" fluid/>
                {/*<Image src="http://blog.imgur.com/wp-content/uploads/2020/01/harold-0.jpg" fluid/>*/}
                <Search/>
            </div>
        );
    }
}

export default Home;