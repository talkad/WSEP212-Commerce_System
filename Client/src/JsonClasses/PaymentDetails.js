

class PaymentDetails {

    constructor(card_number, expiration, ccv, holder, id) {
        this.card_number = card_number;
        this.ccv = ccv;
        this.holder = holder;
        this.id = id;

        const date_split = expiration.split('/');
        this.month = date_split[0];
        this.year = date_split[1];
    }
}

export default PaymentDetails;