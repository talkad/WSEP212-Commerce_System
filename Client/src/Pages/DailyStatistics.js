import React from "react";
import DatePicker from 'react-datepicker';

import "react-datepicker/dist/react-datepicker.css";
import {Spinner} from "react-bootstrap";
import Connection from "../API/Connection";
import {Bar, Pie} from 'react-chartjs-2'


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
    }

    componentDidMount() {
        Connection.sendGetDailyStatistics(this.formatDate(this.state.selectedDate)).then(this.handleDailyStatistics, Connection.handleReject);
    }

    handleDailyStatistics(result) {
        console.log("hello there");
        console.log(result);
        console.log("hi there");
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
                        backgroundColor: ['rgba(255, 99, 132, 1)',
                            'rgba(255, 159, 64, 1)',
                            'rgba(255, 205, 86, 1)',
                            'rgba(75, 192, 192, 1)',
                            'rgba(54, 162, 235, 1)',],
                        data: data
                    }
                ]
            }

            this.setState({loaded: true, pieData: pieData, barData: barData});
        } else {
            alert(result.errMsg);
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
        Connection.sendGetDailyStatistics(this.formatDate(date)).then(this.handleDailyStatistics, Connection.handleReject);
        this.setState({loaded: false, data: [], labels: [], datasets: [], selectedDate: date});
    }

    render() {
        return (
            <div id="daily_statistics_page">
                <div id="daily_statistics_top">
                    <h1>Daily Statistics</h1>
                    <DatePicker selected={this.state.selectedDate} onChange={(date) => this.onChangeDate(date)}/>
                </div>
                {!this.state.loaded && <Spinner animation="grow"/>}
                {this.state.loaded && <div id="daily_statistics_info">
                    <div id="daily_statistics_pie_chart">
                        <Pie
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
                                }
                            }}
                        />
                    </div>
                    <div id="daily_statistics_bar_chart">
                        <Bar
                            data={this.state.barData}
                            options={{
                                title: {
                                    display: true,
                                    text: "no idea",
                                    fontSize: 20
                                },
                                legend: {
                                    display: true,
                                    position: 'right'
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