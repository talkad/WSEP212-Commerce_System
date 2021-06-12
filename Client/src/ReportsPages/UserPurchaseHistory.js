// import React from "react";
// import StaticUserInfo from "../API/StaticUserInfo";
// import Connection from "../API/Connection";
//
// class UserPurchaseHistory extends React.Component{
//     constructor(props) {
//         super(props);
//         this.state = {
//             functionName: 'getUserPurchaseHistory',
//             adminName: window.sessionStorage.getItem('username'),
//             userName: '',
//             showMessage: false,
//             toShow: 'ghi' //TODO: this f** newline doesnt workkkkk
//         };
//     }
//
//     onButtonClickHandler = (e) => {
//         e.preventDefault();
//
//         Connection.sendReportRequest(this.state.functionName, this.state.adminName, this.state.storeId).then(this.handleReportResponse, Connection.handleReject())
//
//         this.setState({showMessage: true});
//         //e.preventDefault();
//     };
//
//     handleReportResponse(result){
//         if(!result.response.isFailure){
//             this.setState({toShow: result.response.result.toString()})//TODO check
//         }
//         else{
//             alert(result.response.errMsg);
//         }
//     }
//
//     handleInputChange = (e, name) => {
//         this.setState({
//             [name]: e.target.value
//         })
//     }
//
//     render(){
//         return(
//             <form>
//                 <h1>Purchase History Details For User Page </h1>
//                 <div> <label> Admin Name : <input readOnly value = {this.state.adminName} className = "adminName" type = "text" onChange = {(e) => this.handleInputChange(e, 'adminName')}/> </label> </div>
//                 <div> <label> User Name : <input className = "username" type = "text" onChange = {(e) => this.handleInputChange(e, 'username')}/> </label> </div>
//                 <div className="toShow"> {this.state.showMessage && <p> {this.state.toShow} </p>}
//                 <button type = "button" onClick = {(e) => this.onButtonClickHandler(e)}> Show History </button>
//                 </div>
//             </form>
//         )
//     }
// }
//
// export default UserPurchaseHistory;