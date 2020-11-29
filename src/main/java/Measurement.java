import java.util.ArrayList;
import java.util.List;

public class Measurement {
    //_____MUUTUJAD_____
    private int columnCount;
    private int rowCount;
    private final int currentGlobalRowCount;

    private String[][] measurementGroupTable;
    private final List<Cell> cells;

    //______KONSTRUKTOR_______
    public Measurement(List<String> measurementGroup) {
        this.currentGlobalRowCount = SorterCSV.getDefineRow(); //Saadakse
        this.measurementGroupTable = createTable(measurementGroup);
        this.cells = defineCells();
    }

    //_______GET_JA_SET_MEETODID______
    public List<Cell> getCells() {
        return cells;
    }

    //______MEETODID______
    private String[][] createTable(List<String> measurement) {
        //Võetakse tabeli mõõtmiste grupp ja luukase tulpade kaupa String massiiv
        List<String[]> columns = new ArrayList<>();
        for (String s : measurement) {
            String[] column = s.split(Character.toString(SorterCSV.getSeparator()));
            columns.add(column);
        }
        //Luuakse vastavalt tabeli suurusele kahe mõõtmeline massiiv
        columnCount = columns.get(0).length;
        rowCount = measurement.size();
        measurementGroupTable = new String[columnCount][rowCount];
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                measurementGroupTable[j][i] = columns.get(i)[j];
            }
        }
        return measurementGroupTable;
    }

    private List<Cell> defineCells() {
        List<Cell> cells = new ArrayList<>();

        //Luukase eraldi lahtrid millele antakse pealkiri kuhu alla läheb ja mis reale läheb
        String currentHeading;
        for (int i = 0; i < columnCount; i++) {
            currentHeading = measurementGroupTable[i][0]; //esimene rida alati peakiri
            for (int j = 0; j < rowCount; j++) {
                int rowValue = currentGlobalRowCount + j;
                if (j != 0) {
                    Cell cell = new Cell(currentHeading, measurementGroupTable[i][j], rowValue);
                    cells.add(cell);
                }
            }
        }
        //Lisatakse globaalsele ridadele juurde praeguse tabeli grupi read
        SorterCSV.setDefineRow(currentGlobalRowCount + rowCount - 1);
        return cells;
    }

    public List<String> getHeadings() {
        //Luukase praeguse tabeli pealkirjadest list ja hiljem liidetakse kõikide tabelite omad kokku
        List<String> headings = new ArrayList<>();
        for (int i = 0; i < columnCount; i++)
            headings.add(measurementGroupTable[i][0]);
        return headings;
    }

}