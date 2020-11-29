import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SorterCSV {

    //_____MUUTUJAD_____
    private static List<Measurement> measurements;
    private static List<String> readFile;
    private static List<ColumnStorage> columnStorage;
    private static String[] headings;
    private static String[][] finalTable;
    private static int columnCount;
    private static int rowCount;
    private static int defineRow;
    private static char separator;

    //_______GET_JA_SET_MEETODID______
    public static String[] getHeadings() {
        return headings;
    }
    public static int getDefineRow() {
        return defineRow;
    }
    public static void setDefineRow(int defineRow) {
        SorterCSV.defineRow = defineRow;
    }
    public static List<ColumnStorage> getColumnStorage() {
        return columnStorage;
    }
    public static char getSeparator() {
        return separator;
    }

    //______MEETODID______
    //Alustatakse sorteermisega ja kontrolitakse kas faili sisu sobib sorteerimiseks - Selles meetodis tehakse üle 90% programmi tööst (väljakutsetest)
    public static void startSorting(File fileToSort) throws IOException {
        defineRow = 0;
        readFile = readFile(fileToSort); // Fail käiakse läbi

        //Kui faili sisu sobib sorteerimiseks
        if (readFile.size() >= 2 && readFile.get(0).charAt(0) == 'F') {
            measurements = defineMeasurements(readFile);
            joinHeadings();
            createTable2DArray();
            columnStorage = storeTableInColumns();
            TablePreferences.display("Tabeli seaded", true); //Kui sorteeritud, siis avatakse tabeli aken
        } else {
            AlertBox.display("Viga!", "Antud fail ei sobi sorteerimiseks!", true);
            clearAll();
        }
    }

    //Fail loetakse läbi
    private static List<String> readFile(File file) throws IOException {
        List<String> readThroughFile = new ArrayList<>();

        //Leitakse separaator , või ;
        try (Scanner sc = new Scanner(file, StandardCharsets.UTF_8)) {
            char[] chars = sc.nextLine().toCharArray();
            for (char c : chars) {
                if (c == ',' || c == ';') {
                    separator = c;
                    break;
                }
            }
        }

        //Loetakse kõik read failist läbi, mis ei sisalda päiseid või tühjasi ridu
        try (Scanner sc = new Scanner(file, StandardCharsets.UTF_8)) {
            while (sc.hasNextLine()) {
                String row = sc.nextLine();
                if (row.charAt(0) != separator) {
                    readThroughFile.add(row); //Lisatakse kõik read failist, mis pole tühjad
                    if (row.charAt(0) != 'F') {
                        rowCount++; //Vaadatakse mitu rida tuleb uude faili kokku, et saada õiged numbrid mõõtmistulemuste valimisel, päiseid ei arvestata
                    }
                }
            }
        }
        rowCount++; //Pealkirjade jaoks listakse ekstra rida, sest päiste jaoks tuleb 1 rida jurde lõppfailis
        return readThroughFile;
    }


    //Defineeritakse kõik mõõtmised ehk iga mõõtmine on 1 päise rida ja selle all olevad mõõtmised
    private static List<Measurement> defineMeasurements(List<String> readFile) {

        // Siia paigutatakse päisete str (File#, Name, Operator...) + data str (1370, 10/04/2019 10:56, Supervisor...). See list sisaldab alati max 2 Stringi.
        List<String> lines = new ArrayList<>();
        // Siia paigutatakse komplektist "päised + data" loodud objektid.
        List<Measurement> measurements = new ArrayList<>();

        for (int i = 0; i < readFile.size(); i++) {
            String line = readFile.get(i);
            if (i != 0 && line.charAt(0) == 'F') {
                measurements.add(new Measurement(lines)); // Iga komplekti (päised str + data str) kohta luuakse objekt
                lines.clear();
            }
            lines.add(line);
            App.setProgressValue(i, readFile.size());
        }
        measurements.add(new Measurement(lines)); //Lisab viimase ka alati
        return measurements;
    }

    //Käiakse läbi ja võetakse kõikide mõõtmiste päised ning pannakse kokku
    private static void joinHeadings() {
        List<String> allHeadings = new ArrayList<>();
        for (Measurement headings : measurements) {
            allHeadings.addAll(headings.getHeadings());
        }
        //Luuakse list ning sorteeritakse list nii, et korduvaid päiseid ei oleks
        List<String> distinctHeadings;
        distinctHeadings = allHeadings.stream().distinct().collect(Collectors.toList());
        headings = new String[distinctHeadings.size()];
        for (int i = 0; i < headings.length; i++)
            headings[i] = distinctHeadings.get(i);
        columnCount = headings.length; //Määratakse veergude arv
    }

    //Leitakse veeru indeks listis päise nime kaudu
    public static int findColumn(String heading) {
        for (int i = 0; i < headings.length; i++) {
            if (heading.equals(headings[i])) {
                return i;
            }
        }
        return -1;
    }

    //Tehakse kahe mõõtmeline massiiv
    private static void createTable2DArray() {
        finalTable = new String[columnCount][rowCount];

        //Käiakse läbi kõik mõõtmised ja mõõtmiste klassis olevad lahtrid ning lisatakse lahtird õige koha peale massiivis
        for (Measurement measurement : measurements) {
            for (Cell cell : measurement.getCells()) {
                finalTable[findColumn(cell.getHeading())][cell.getRowValue()] = (cell.getCellValue() == null) ? "" : cell.getCellValue();
            }
        }
        //Lisab pealkirjad, sellepärast lisati readFile() ekstra pealkirja rida
        for (int i = 0; i < columnCount; i++) {
            finalTable[i][0] = headings[i] + separator;
        }
    }

    //Salvestatakse kõik andmed kahe mõõtmilisest massiivist tulpadeks
    private static List<ColumnStorage> storeTableInColumns() {
        List<ColumnStorage> columnStorage = new ArrayList<>();
        List<String> rowValues = new ArrayList<>();

        for (int i = 0; i < columnCount; i++) {
            for (int j = 0; j < rowCount; j++) {
                if (j != 0) // Et extra päiseid ei tuleks esimesele reale
                    rowValues.add(finalTable[i][j]);
            }
            columnStorage.add(new ColumnStorage(headings[i], rowValues));
            rowValues.clear();
        }
        rowCount = columnStorage.get(0).getValues().length;
        return columnStorage;
    }

    //Muudetakse muutujad algseks kui minnakse tagasi
    public static void clearAll() {
        columnCount = 0;
        rowCount = 0;
        defineRow = 0;
        measurements.clear();
        readFile.clear();
    }
}