import React from 'react';
import Select from 'react-select';
import StaticUserInfo from "../API/StaticUserInfo";

const DiscountOptions = [
    { value: 'visible', label: 'Visible' },
    { value: 'hidden', label: 'Hidden' },
    { value: 'conditional', label: 'Conditional' },
];

class AddDiscount extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            selectedOption: null,
            functionName: '', //TODO: replace the a,b,c
            productId: '',
            percentage: '',
            duration: '',
            last: '',
        };
    }

    handleOptionChange = selectedOption => {
        this.setState({ selectedOption });

        this.state.selectedOption ==='hidden' ? this.setState({functionName:'a'}) :
            this.state.selectedOption ==='visible' ? this.setState({functionName:'b'}) :
                this.setState({functionName:'c'});


    };

    handleInputChange = (e, name) => {
        this.setState({
            [name]: e.target.value
        })
    }

    handleClick(e) {
        e.preventDefault();
        //TODO: add discount not implemented
    }

    render() {
        const { selectedOption } = this.state;
        return (
            <form>
                <h1>Add Discount Page </h1>
                <Select value={selectedOption} onChange={this.handleOptionChange} options={DiscountOptions}/>
                <div> <label> Product Id : <input className = "productId" type = "text" onChange = {(e) => this.handleInputChange(e, 'productId')}/> </label> </div>
                <div> <label> Percentage : <input className = "percentage" type = "text" onChange = {(e) => this.handleInputChange(e, 'percentage')}/> </label> </div>
                <div> <label> Duration : <input className = "duration" type = "text" onChange = {(e) => this.handleInputChange(e, 'duration')}/> </label> </div>
                <div> <label> Last : <input className = "last" type = "text" placeholder='empty / coupon code / condition' style={{width: "370px"}} onChange = {(e) => this.handleInputChange(e, 'last')}/> </label> </div>
                <button type = "button" onClick = {(e) => this.handleClick(e)}> Add Discount </button>
            </form>
        )
    }
}

export default AddDiscount;