package Dir.dev.mywallet.Model;



public class Category {
    private int categoryId;
    private String categoryName;
    private String categorySign;

    public Category() {
    }

    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategorySign() {
        return categorySign;
    }

    public void setCategorySign(String categorySign) {
        this.categorySign = categorySign;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
