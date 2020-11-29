public class Cell {

    //_____MUUTUJAD_____
    private final String heading; //Lahtrile kuuluv päis, et saada tulba numbrit
    private final String cellValue; //Lahtrile kuuluv väärtus
    private final int rowValue; //Mitmesse ritta lahter kuulub

    //______KONSTRUKTOR_______
    public Cell(String heading, String cellValue, int rowValue) {
        this.heading = heading;
        this.cellValue = cellValue;
        this.rowValue = rowValue;
    }

    //_______GET_JA_SET_MEETODID______
    public String getHeading() {
        return heading;
    }

    public String getCellValue() {
        return cellValue;
    }

    public int getRowValue() {
        return rowValue;
    }

    //______MEETODID______
    @Override
    public String toString() {
        return "Cell{" +
                "heading='" + heading + '\'' +
                ", cellValue='" + cellValue + '\'' +
                '}';
    }
}