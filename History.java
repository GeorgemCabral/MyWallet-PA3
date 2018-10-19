package Diretory.dev.mywallet.Model;



public class History {

    private int historyId;
    private String historyCategory;
    private Double historyAmount;
    private String historySign;
    private String historyDetail;
    private String historyDate;
    private String historyTime;
    private String historyDateTime;

    public History() {
    }

    public History(String historyCategory, Double historyAmount, String historySign, String historyDetail, String historyDate, String historyTime, String historyDateTime) {
        this.historyCategory = historyCategory;
        this.historyAmount = historyAmount;
        this.historySign = historySign;
        this.historyDetail = historyDetail;
        this.historyDate = historyDate;
        this.historyTime = historyTime;
        this.historyDateTime = historyDateTime;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public String getHistoryCategory() {
        return historyCategory;
    }

    public void setHistoryCategory(String historyCategory) {
        this.historyCategory = historyCategory;
    }

    public Double getHistoryAmount() {
        return historyAmount;
    }

    public void setHistoryAmount(Double historyAmount) {
        this.historyAmount = historyAmount;
    }

    public String getHistorySign() {
        return historySign;
    }

    public void setHistorySign(String historySign) {
        this.historySign = historySign;
    }

    public String getHistoryDetail() {
        return historyDetail;
    }

    public void setHistoryDetail(String historyDetail) {
        this.historyDetail = historyDetail;
    }

    public String getHistoryDate() {
        return historyDate;
    }

    public void setHistoryDate(String historyDate) {
        this.historyDate = historyDate;
    }

    public String getHistoryTime() {
        return historyTime;
    }

    public void setHistoryTime(String historyTime) {
        this.historyTime = historyTime;
    }

    public String getHistoryDateTime() {
        return historyDateTime;
    }

    public void setHistoryDateTime(String historyDateTime) {
        this.historyDateTime = historyDateTime;
    }
}
