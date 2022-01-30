/**
 * 翻译后的实例对象
 */
public class Entry {

    /**
     * 第一个字符
     */
    private String charOne;

    /**
     * 第二个字符
     */
    private String charTwo;

    public Entry(String charOne, String charTwo) {
        this.charOne = charOne;
        this.charTwo = charTwo;
    }

    public String getCharOne() {
        return charOne;
    }

    public void setCharOne(String charOne) {
        this.charOne = charOne;
    }

    public String getCharTwo() {
        return charTwo;
    }

    public void setCharTwo(String charTwo) {
        this.charTwo = charTwo;
    }
}
