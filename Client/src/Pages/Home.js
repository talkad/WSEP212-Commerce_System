import Search from "../Components/Search";
import React from "react";
import {Button, Container, Image} from "react-bootstrap";
import Modal from 'react-modal'
import harold_gif from '../Images/harold_home_page.gif'


class Home extends React.Component{
    constructor(props) {
        super(props);

    }

    render() {
        return(
            <div>
                <h1>welcome to the very cool commerce system</h1>
                <Image src={harold_gif} fluid/>
                <Search/>
            </div>
        );
    }
}

export default Home;