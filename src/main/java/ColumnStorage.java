import java.util.List;

public class ColumnStorage {
    //_____MUUTUJAD_____
    private final String heading; //Tulba päis
    private String[] values; //Tulpa kuuluvad andmed ridade kaupa

    //______KONSTRUKTOR_______
    public ColumnStorage(String heading, List<String> values) {
        this.heading = heading;
        this.values = listToArray(values);
    }

    //_______GET_JA_SET_MEETODID______
    public String getHeading() {
        return heading;
    }

    public String[] getValues() {
        return values;
    }

    //______MEETODID______
    //List tehakse massiiviks
    private String[] listToArray(List<String> valuesList) {
        values = new String[valuesList.size()];
        return valuesList.toArray(values);
    }

    //Et saada vahemiku numbrit tabeli aknas createTable() ja buildData()
    public int getElementIndex(String element) {
        int index = -1;
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(element)) {
                index = i;
            }
        }
        return index;
    }
}