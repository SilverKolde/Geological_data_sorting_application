
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TablePreferences {

    //_____MUUTUJAD_____
    private static final List<CheckBox> checkBoxes = new ArrayList<>();
    private static final List<ColumnStorage> finalColumnStorage = new ArrayList<>();
    private static TableView<ObservableList<String>> tableView = new TableView<>();
    private static VBox layoutV;
    private static final VBox table = new VBox(10);
    private static Button saveButton;
    private static FileChooser fileChooseSave;
    private static Stage window;
    private static ChoiceBox<String> rangeToShow;
    private static TextField lastMeasurementCount;
    private static TextField from;
    private static TextField to;

    //______MEETODID______
    public static void display(String title, boolean modality) {
        window = new Stage();
        window.getIcons().add(new Image("Icon.png"));
        if (modality)
            window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(500);
        window.setMinHeight(300);
        window.setOnCloseRequest(e -> {
            e.consume();
            clearAll();
        });

        //Valiku kastide pealkirja silt
        Label checkBoxTitle = new Label("Vali soovitud tulbad:");
        checkBoxTitle.setStyle("-fx-font-size: 9pt;");

        TilePane tilePane = new TilePane();

        //Valiku kastid luuakse nii et päis Cal Check oleks alati lõpus
        boolean isCalCheck = false;
        for (String heading : SorterCSV.getHeadings()) {
            if (!heading.equals("Cal Check")) {
                CheckBox checkBox = new CheckBox(heading);
                checkBox.setSelected(true);
                checkBoxes.add(checkBox);
                tilePane.getChildren().add(checkBox);

                //Päis nimega "File #" peab olema alati selekteeritud
                if (heading.equals("File #")) {
                    checkBox.setDisable(true);
                }
            } else {
                isCalCheck = true;
            }
        }

        if (isCalCheck) {
            CheckBox calCheck = new CheckBox("Cal Check");
            calCheck.setSelected(true);
            checkBoxes.add(calCheck);
            tilePane.getChildren().add(calCheck);
        }

        //Luukase valiku kast, mida vajutades kas selekteerib (või vastupidi) kõik valiku kastid
        CheckBox checkBoxMaster = new CheckBox("Vali kõik");
        checkBoxMaster.setSelected(true);
        checkBoxMaster.setStyle("mark-color: #51ace0;");
        tilePane.getChildren().add(checkBoxMaster);
        checkBoxMaster.setOnAction(e -> selectAll(checkBoxMaster.isSelected()));

        //Paigutatakse kastid ilusti
        tilePane.setPadding(new Insets(10, 0, 0, 0));
        tilePane.setVgap(4);
        tilePane.setHgap(4);
        tilePane.setPrefColumns(12);
        tilePane.setPrefTileWidth(100);
        tilePane.setTileAlignment(Pos.BASELINE_LEFT);
        tilePane.setAlignment(Pos.BASELINE_LEFT);

        VBox layoutCheckbox = new VBox();
        layoutCheckbox.getChildren().addAll(checkBoxTitle, tilePane);
        Group checkBoxGroup = new Group(layoutCheckbox);

        //Mõõtmiste valiku silt
        Label rowChoiceTitle = new Label("Vali mõõtmised, mida kuvada:");
        rowChoiceTitle.setStyle("-fx-font-size: 9pt;");

        //Kõik võimalikud valikud mõõtmiste kuvamiseks
        rangeToShow = new ChoiceBox<>();
        rangeToShow.getItems().add("Vali kõik mõõtmised");
        rangeToShow.getItems().add("Vali viimased mõõtmised");
        rangeToShow.getItems().add("Vali vahemikus mõõtmisi");
        rangeToShow.setValue("Vali kõik mõõtmised");
        rangeToShow.setMinSize(180, 31.5);

        lastMeasurementCount = new TextField();
        lastMeasurementCount.setStyle("-fx-font-size: 10pt;");

        from = new TextField();
        from.setStyle("-fx-font-size: 10pt;");

        to = new TextField();
        to.setStyle("-fx-font-size: 10pt;");

        //Et sisestada saaks ainult numbreid
        lastMeasurementCount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                lastMeasurementCount.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        from.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                lastMeasurementCount.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        to.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                lastMeasurementCount.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        //Paigutamine
        HBox rowValues = new HBox(10);
        rowValues.getChildren().add(rangeToShow);

        VBox layoutRowChoiceY = new VBox(10);
        layoutRowChoiceY.getChildren().addAll(rowChoiceTitle, rowValues);
        layoutRowChoiceY.setAlignment(Pos.CENTER_LEFT);

        Pane layoutRowChoicePane = new Pane();
        layoutRowChoicePane.getChildren().addAll(layoutRowChoiceY);

        Group rowChoiceGroup = new Group(layoutRowChoicePane);

        rangeToShow.setOnAction(e -> selectRows(from, to, rowValues));


        //Nupud
        //Tagasi nupp, et minna programmi algusesse tagasi
        Button closeButton = new Button("Tagasi");
        closeButton.setStyle("-fx-font-size: 12pt; -fx-font-weight: bold; -fx-pref-height: 20; -fx-background-radius: 4; -fx-background-insets: 3, 0;");
        closeButton.setOnAction(e -> {
            e.consume();
            App.setProgressValue(0, 0);
            App.getInfo().setText("Info: Käsi-XRF faili sorteerija");
            clearAll();
        });

        //Loo tabel nupp, et seadistuset muutmisel tabel uuendada
        Button continueButton = new Button("Loo tabel");
        continueButton.setId("startbutton");
        continueButton.setOnAction(e -> createTable());

        //Faili salvestamise nupp
        fileChooseSave = new FileChooser();
        fileChooseSave.setTitle("Salvesta fail nimega");
        fileChooseSave.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Table files", "*.csv"));

        saveButton = new Button("Salvesta");
        saveButton.setDisable(true);
        saveButton.setId("savefilebutton");
        saveButton.setOnAction(e -> {
            try {
                writeCSV();
                clearAll();
            } catch (NullPointerException exeption) {
                // Kasutaja loobus salvestamisest ja tekkis NullPointerException.
                // Püüame kinni ja jätame tähelepanuta, siis jääb kasutajale salvestamata tabel alles.
            } catch (IOException ex) {
                // Pole suutnud seda olukorda tekitada. Viskan veateate akna ja väljastame täpsema info konsooli.
                AlertBox.display("Viga!", "Faili loomisel/faili kirjutamisel tekkis seni teadmata viga.", true);
                ex.printStackTrace();
            }
        });

        HBox buttons = new HBox();
        buttons.getChildren().addAll(closeButton, continueButton, saveButton);

        //Kogu akna paigutus
        layoutV = new VBox(15);
        layoutV.getChildren().addAll(checkBoxGroup, rowChoiceGroup, buttons);
        layoutV.setAlignment(Pos.CENTER_LEFT);
        layoutV.setPadding(new Insets(10, 20, 10, 20));

        Group finalGroup = new Group(layoutV);
        ScrollPane finalLayout = new ScrollPane(finalGroup);
        finalLayout.setFitToHeight(true);
        finalLayout.setFitToWidth(true);

        Scene scene = new Scene(finalLayout);

        scene.getStylesheets().add("TU_THEME.css");
        window.setScene(scene);

        window.setWidth(1375);
        window.setMinWidth(400);
        window.setHeight(850);
        window.setMinHeight(300);

        window.show();
    }

    //Valiku kasti jaoks meetod, mis selekteerib kõik või vastupidi
    private static void selectAll(boolean isSelected) {
        for (CheckBox checkBox : checkBoxes) {
            if (!checkBox.getText().equals("File #")) {
                checkBox.setSelected(isSelected);
            }
        }
    }

    //Näidatakse ainult neid teksti kirjutamise lahtreid, mis mõõtmiste kuvamise jaoks on
    private static void selectRows(TextField from, TextField to, HBox layout) {
        switch (rangeToShow.getValue()) {
            case "Vali kõik mõõtmised":
                layout.getChildren().clear();
                layout.getChildren().add(rangeToShow);
                break;
            case "Vali viimased mõõtmised":
                layout.getChildren().clear();
                layout.getChildren().addAll(rangeToShow, lastMeasurementCount);
                lastMeasurementCount.setPromptText("Max: " + SorterCSV.getColumnStorage().get(0).getValues().length);
                break;
            case "Vali vahemikus mõõtmisi":
                layout.getChildren().clear();
                layout.getChildren().addAll(rangeToShow, from, to);
                // Saadakse esimese mõõtmise nr
                from.setPromptText("Esimene: " + SorterCSV.getColumnStorage().get(0).getValues()[0]);
                // ja viimase mõõtmise nr
                to.setPromptText("Viimane: " + SorterCSV.getColumnStorage().get(0).getValues()[SorterCSV.getColumnStorage().get(0).getValues().length - 1]);
                break;
        }
    }

    //Meetod nupu Loo tabel jaoks, mille tulemusel luuakse uus tabel võttes arvesse seadistusi
    private static void createTable() {
        //Eelmine tabel kustutatakse
        finalColumnStorage.clear();
        layoutV.getChildren().remove(table);
        table.getChildren().clear();

/**********************************************************************************************************************
 * Siin on veel error sees:
 *          1) Loo tabel
 *          2) See tabel ei sobinud. Nüüd täpsusta uuesti vahemikku, kui tee viga sisse (viimased 1000 mõõtmist, vahemik alates 0-st jne jne)
 *
 *          Error. tabel kaob aknast ära (aga andmed on ikka listides olemas, salvestada saab).
 *          Ei määrata õigeid andmeid uue tabeli loomiseks, sest vana info on muutujates veel alles.
 *          Võib-olla peab tooma sisse nupu 'uus tabel', mis kustutab vanad andmed.
 *
 *          Samas vb teha nii, et kui tabel on ette kuvatud, muutub tekst 'Loo tabel' ==> 'Uus tabel'. 'vali mõõtmised' sektsioon set.Disable=true;
 *          Kui vajutad 'Uus tabel' siis tekst jälle muutub ja saad uue vahemiku valida, mis andmeid näha tahad. Aga täna pole ilmselt rohkem aega tegeleda.
 *
 * ********************************************************************************************************************/

        boolean isCorrect = true;

        //Valib välja need tulbad, mis selekteeritud
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                for (ColumnStorage columnStorage : SorterCSV.getColumnStorage()) {
                    if (checkBox.getText().equals(columnStorage.getHeading())) {
                        finalColumnStorage.add(columnStorage);
                        break;
                    }
                }
            }
        }

        //Valitakse välja mõõtmised, mida näidata

        //Kui tahetakse viimaseid mõõtmisi
        if (rangeToShow.getValue().equals("Vali viimased mõõtmised")) {
            if (lastMeasurementCount.getText().equals("")) {
                AlertBox.display("Viga!", "Väärtus on sisestamata!", true);
                isCorrect = false;
            } else {
                int insertedValue = Integer.parseInt(lastMeasurementCount.getText());
                int maxValue = SorterCSV.getColumnStorage().get(0).getValues().length;

                //Kui valikud ei ole sobilikud visatakse ette veaaken
                if (insertedValue <= 0) {
                    AlertBox.display("Viga!", "Väärtus peab olema suurem kui 0!", true);
                    isCorrect = false;
                } else if (insertedValue > maxValue) {
                    AlertBox.display("Viga!", "Väärtus ei tohi olla suurem kui " + maxValue + "!", true);
                    isCorrect = false;
                } else {
                    isCorrect = true;
                }
            }

            //Kui tahetakse vahemikus mõõtmisi
        } else if (rangeToShow.getValue().equals("Vali vahemikus mõõtmisi")) {
            int start = finalColumnStorage.get(0).getElementIndex(from.getText());
            int end = finalColumnStorage.get(0).getElementIndex(to.getText()) + 1;

            //Kui valikud ei ole sobilikud visatakse ette veaaekn
            if (start == -1) {
                AlertBox.display("Viga!", "Alguse väärtus ei vasta nõuetele!", true);
                isCorrect = false;
            } else if (end == 0) {
                AlertBox.display("Viga!", "Lõpu väärtus ei vasta nõuetele!", true);
                isCorrect = false;
            } else if (start > end) {
                AlertBox.display("Viga!", "Alguse väärtus peab tabelis esinema enne lõpu väärtust!", true);
                isCorrect = false;
            } else {
                isCorrect = true;
            }
        }

        //Luuakse tabel kasutades javafx tabeli võimalusi, kui ridade valimisel pole tehtud vigu
        if (isCorrect) {
            tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            tableView = createTableView(finalColumnStorage);

            table.getChildren().add(tableView);
            table.setPrefWidth(1300);
            layoutV.getChildren().add(table);

            saveButton.setDisable(false);
        }
    }

    //Lisatakse kõik andmed javafx tabeli lahtritesse ja käiakse läbi ColumnStorage päised ning lisatakse tabeli päistena
    // buildData() ja createTableView() meetodi põhimõtte näited sain netist, et javafx tabelit luua
    private static TableView<ObservableList<String>> createTableView(List<ColumnStorage> dataArray) {
        TableView<ObservableList<String>> tableView = new TableView<>();
        tableView.setItems(buildData(dataArray));

        for (int i = 0; i < dataArray.size(); i++) {
            final int curCol = i;
            final TableColumn<ObservableList<String>, String> column = new TableColumn<>(dataArray.get(i).getHeading());
            column.setReorderable(false);
            column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(curCol)));
            tableView.getColumns().add(column);
        }

        return tableView;
    }

    //Javafx tabeli jaoks observablelist kus käiakse kõik ColumnStorage läbi ja võetakse andmed ning lisatakse ridade
    //kaupa observable listi
    private static ObservableList<ObservableList<String>> buildData(List<ColumnStorage> dataArray) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        int start = 0;
        int end = dataArray.get(0).getValues().length;

        if (rangeToShow.getValue().equals("Vali viimased mõõtmised")) {
            start = end - Integer.parseInt(lastMeasurementCount.getText());
        } else if (rangeToShow.getValue().equals("Vali vahemikus mõõtmisi")) {
            start = dataArray.get(0).getElementIndex(from.getText());
            end = dataArray.get(0).getElementIndex(to.getText()) + 1;
        }

        String[] row = new String[dataArray.size()];
        for (int i = start; i < end; i++) { //Käib läbi read
            for (int j = 0; j < dataArray.size(); j++) { //Käib läbi tulbad
                row[j] = dataArray.get(j).getValues()[i];
            }
            if (row[0].equals(""))
                break;
            data.add(FXCollections.observableArrayList(row));
        }
        return data;
    }

    //Nupu salvesta jaoks meetod. Kirjutatakse uus csv fail valitud kohta
    private static void writeCSV() throws IOException {
        String separator = Character.toString(SorterCSV.getSeparator());
        File file = fileChooseSave.showSaveDialog(window);

        try (BufferedWriter buffer = new BufferedWriter(new FileWriter(file.getAbsolutePath()))) {
            for (ColumnStorage column : finalColumnStorage) {
                buffer.write(column.getHeading() + separator);
            }
            buffer.write("\n");
            
            for (Object item : tableView.getItems()) {
                String row = item.toString();
                row = row.replace(",", separator);
                buffer.write(row.substring(1, row.length() - 1) + "\n");
            }
        }
    }

    //Muudetakse muutujad algseks kui minnakse tagasi
    private static void clearAll() {
        finalColumnStorage.clear();
        SorterCSV.clearAll();
        window.close();
    }
}