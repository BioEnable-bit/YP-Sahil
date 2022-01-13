package com.yespustak.yespustakapp.api.request;

import com.google.gson.annotations.SerializedName;
import com.yespustak.yespustakapp.models.PaymentModel;

import java.util.List;

public class SavePaymentRequest extends PaymentModel {

    @SerializedName("payment_id")
    private String paymentId;

    @SerializedName("book_id")
    private List<String> bookIds;

    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("payment_status")
    private String paymentStatus;

    public SavePaymentRequest() {
    }

    public SavePaymentRequest(PaymentModel payment) {
        this.paymentId = payment.getId();
        this.setId(payment.getId());
        this.setAmount(payment.getAmount());
        this.setCurrency(payment.getCurrency());
        this.setOrderId(payment.getOrderId());
        this.setInvoiceId(payment.getInvoiceId());
        this.setInternational(payment.isInternational());
        this.setMethod(payment.getMethod());
        this.setAmountRefunded(payment.getAmountRefunded());
        this.setRefundStatus(payment.getRefundStatus());
        this.setPaymentStatus(payment.getStatus());
        this.setCaptured(payment.isCaptured());
        this.setDescription(payment.getDescription());
        this.setCardId(payment.getCardId());
        this.setBank(payment.getBank());
        this.setWallet(payment.getWallet());
        this.setVpa(payment.getVpa());
        this.setEmail(payment.getEmail());
        this.setContact(payment.getContact());
        this.setCustomerId(payment.getCustomerId());
        this.setFee(payment.getFee());
        this.setTax(payment.getTax());
        this.setErrorCode(payment.getErrorCode());
        this.setErrorDescription(payment.getErrorDescription());
        this.setErrorSource(payment.getErrorSource());
        this.setErrorStep(payment.getErrorStep());
        this.setErrorReason(payment.getErrorReason());
        this.setCreatedAt(payment.getCreatedAt());
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public List<String> getBookIds() {
        return bookIds;
    }

    public void setBookIds(List<String> bookIds) {
        this.bookIds = bookIds;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
