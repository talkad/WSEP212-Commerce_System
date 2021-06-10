import React from "react";
import DatePicker from 'react-datepicker';

import "react-datepicker/dist/react-datepicker.css";
import {Image, Spinner} from "react-bootstrap";
import Connection from "../API/Connection";
import {Bar, Pie} from 'react-chartjs-2'
import timemachine from "../Images/timemachine.png";


class DailyStatistics extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            selectedDate: new Date(),
            pieData: [],
            barData: [],
            loaded: false,
        }

        this.handleDailyStatistics = this.handleDailyStatistics.bind(this);
        this.midHandleDailyStatistics = this.midHandleDailyStatistics.bind(this);
    }

    componentDidMount() {
        Connection.sendGetDailyStatistics(this.formatDate(this.state.selectedDate)).then(this.handleDailyStatistics, Connection.handleReject);
    }

    handleDailyStatistics(result) {
        console.log("in daily statistics");
        console.log(result);
        if (!result.isFailure) {

            let processedData = [];

            let preProcessData = function(rawData){
                let split = rawData.split(':');
                processedData.push({label: split[0], number: parseInt(split[1])})
            }

            result.result.map( entry => preProcessData(entry) );

            let labels = [];
            let data = [];
            //let backgroundColor = [];

            let random_rgba = function() {
                var o = Math.round, r = Math.random, s = 255;
                return 'rgba(' + o(r()*s) + ',' + o(r()*s) + ',' + o(r()*s) + ',' + r().toFixed(1) + ')';
            }

            let processData = function(label, number){
                labels.push(label);
                data.push(number);
                //backgroundColor.push(random_rgba());
            }

            processedData.map(({label, number}) => processData(label, number));

            let pieData = {
                labels: labels,
                datasets: [
                    {
                        backgroundColor: ['rgba(255, 99, 132, 1)',
                            'rgba(255, 159, 64, 1)',
                            'rgba(255, 205, 86, 1)',
                            'rgba(75, 192, 192, 1)',
                            'rgba(54, 162, 235, 1)',],
                        data: data
                    }
                ]
            }

            let barData = {
                labels: labels,
                datasets: [
                    {
                        label: "Guest",
                        backgroundColor: ['rgba(255, 99, 132, 1)'],
                        data: [data[0], 0, 0, 0, 0]
                    },
                    {
                        label: "Registered",
                        backgroundColor: [
                            'rgba(255, 159, 64, 1)',
                            ],
                        data: [0, data[1], 0, 0, 0]
                    },
                    {
                        label: "Manager",
                        backgroundColor: [
                            'rgba(255, 205, 86, 1)',],
                        data: [0, 0, data[2], 0, 0]
                    },
                    {
                        label: "Owner",
                        backgroundColor: [
                            'rgba(75, 192, 192, 1)',
                            ],
                        data: [0, 0, 0, data[3], 0]
                    },
                    {
                        label: "Admin",
                        backgroundColor: [
                            'rgba(54, 162, 235, 1)',],
                        data: [0, 0, 0, 0, data[4]]
                    },
                ]
            }

            this.setState({loaded: true, pieData: pieData, barData: barData});

            if(this.formatDate(this.state.selectedDate) === this.formatDate(new Date())){
                Connection.getLiveUpdate().then(this.midHandleDailyStatistics, Connection.handleReject);
            }
        } else {
            alert(result.errMsg);
            this.setState({loaded: true, pieData: [], barData: []});
        }
    }

    midHandleDailyStatistics(result){
        if(this.formatDate(this.state.selectedDate) === this.formatDate(new Date())){
            let processedData = [];

            let preProcessData = function(rawData){
                let split = rawData.split(':');
                processedData.push({label: split[0], number: parseInt(split[1])})
            }

            JSON.parse(result.message).map( entry => preProcessData(entry) );

            let labels = [];
            let data = [];
            //let backgroundColor = [];

            let random_rgba = function() {
                var o = Math.round, r = Math.random, s = 255;
                return 'rgba(' + o(r()*s) + ',' + o(r()*s) + ',' + o(r()*s) + ',' + r().toFixed(1) + ')';
            }

            let processData = function(label, number){
                labels.push(label);
                data.push(number);
                //backgroundColor.push(random_rgba());
            }

            processedData.map(({label, number}) => processData(label, number));

            let pieData = {
                labels: labels,
                datasets: [
                    {
                        backgroundColor: ['rgba(255, 99, 132, 1)',
                            'rgba(255, 159, 64, 1)',
                            'rgba(255, 205, 86, 1)',
                            'rgba(75, 192, 192, 1)',
                            'rgba(54, 162, 235, 1)',],
                        data: data
                    }
                ]
            }

            let barData = {
                labels: labels,
                datasets: [
                    {
                        label: "Guest",
                        backgroundColor: ['rgba(255, 99, 132, 1)'],
                        data: [data[0], 0, 0, 0, 0]
                    },
                    {
                        label: "Registered",
                        backgroundColor: [
                            'rgba(255, 159, 64, 1)',
                        ],
                        data: [0, data[1], 0, 0, 0]
                    },
                    {
                        label: "Manager",
                        backgroundColor: [
                            'rgba(255, 205, 86, 1)',],
                        data: [0, 0, data[2], 0, 0]
                    },
                    {
                        label: "Owner",
                        backgroundColor: [
                            'rgba(75, 192, 192, 1)',
                        ],
                        data: [0, 0, 0, data[3], 0]
                    },
                    {
                        label: "Admin",
                        backgroundColor: [
                            'rgba(54, 162, 235, 1)',],
                        data: [0, 0, 0, 0, data[4]]
                    },
                ]
            }

            this.setState({loaded: true, pieData: pieData, barData: barData});

            if(this.formatDate(this.state.selectedDate) === this.formatDate(new Date())){
                console.log("going to update again");
                Connection.getLiveUpdate().then(this.midHandleDailyStatistics, Connection.handleReject);
            }
        }
    }


    formatDate(date) {
        var d = new Date(date),
            month = '' + (d.getMonth() + 1),
            day = '' + d.getDate(),
            year = d.getFullYear();

        if (month.length < 2)
            month = '0' + month;
        if (day.length < 2)
            day = '0' + day;

        return [year, month, day].join('-');
    }

    onChangeDate(date) {
        let formattedDate = this.formatDate(date);
        Connection.sendGetDailyStatistics(formattedDate).then(this.handleDailyStatistics, Connection.handleReject).catch(err => console.log("hello there"));
        this.setState({loaded: false, labels: [], datasets: [], selectedDate: date});
    }

    render() {
        return (
            <div id="daily_statistics_page">
                <div id="daily_statistics_top">
                    <h1>Daily Statistics</h1>
                    <DatePicker selected={this.state.selectedDate} onChange={(date) => this.onChangeDate(date)}/>
                </div>
                {this.state.loaded && this.state.pieData.length === 0 && <Image src={timemachine}/>}
                {!this.state.loaded && <Spinner animation="grow"/>}
                {this.state.loaded && this.state.pieData.length !== 0 && <div id="daily_statistics_info">
                    <div id="daily_statistics_pie_chart">
                        <Pie
                            height={450}
                            data={this.state.pieData}
                            options={{
                                title: {
                                    display: true,
                                    text: 'Dunno',
                                    fontSize: 20
                                },
                                legend: {
                                    display: true,
                                    position: 'right'
                                },
                                maintainAspectRatio: false
                            }}
                        />
                    </div>
                    <div id="daily_statistics_bar_chart">
                        <Bar
                            data={this.state.barData}
                            options={{
                                title: {
                                    display: false,
                                    text: "no idea",
                                    fontSize: 20
                                },
                                legend: {
                                    display: false,
                                },
                                scales: {
                                    x: {
                                        stacked: true,
                                    },
                                    y: {
                                        stacked: true,
                                    }
                                }
                            }}
                        />
                    </div>
                </div>}
            </div>
        );
    }
}

export default DailyStatistics;