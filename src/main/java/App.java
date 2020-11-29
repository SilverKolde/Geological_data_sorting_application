import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.shape.*;
import javafx.scene.layout.Pane;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class App extends Application {

    //_____MUUTUJAD_____
    private static ProgressBar progressBar;
    private static Stage window;
    private static Image dropFileImage;
    private static ImageView dropFileView;
    private static Label labelInput;
    private static Label fileName;
    private static Label info;
    private static Button chooseFile;
    private static Button closeButton;
    private static Button startButton;
    private static Pane fileInputPane;
    private static File convertableFile;
    private static boolean fileIsSet = false;

    //_______GET_JA_SET_MEETODID______
    public static ProgressBar getProgressBar() {
        return progressBar;
    }

    public static Label getInfo() {
        return info;
    }

    //______MEETODID______
    @Override
    public void start(Stage primaryStage) throws Exception {

        //Peaaken
        window = primaryStage;
        window.setTitle("Käsi-XRF faili sorteerija");
        window.getIcons().add(new Image("Icon.png"));
        window.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });

        //Programmi info silt
        Label programInfo = new Label("Programmi ülesanne on muuta faili nii et seda on võimalik lihtsamalt kasutada andmete töötlemiseks. \n" +
                "1. Eemaldatakse üleliigsed read. \n" +
                "2. Sorteeritakse kõik andmed kindlate päiste vahel. \n" +
                "3. Antakse võimalus valida, millised päised ja mõõtmised alles jätta. \n" +
                "Alustamiseks lisage Käsi-XRF-st saadud *.csv lõpuga fail üleslaadmise aknasse.");

        //Üleslaadimise kast
        Rectangle dragBox = new Rectangle();
        dragBox.setWidth(600);
        dragBox.setHeight(180);
        dragBox.setId("dragrect");

        //Faili nime silt üleslaadmise kastis
        fileName = new Label("");
        fileName.setStyle("-fx-font-size: 12pt;");
        fileName.setTextAlignment(TextAlignment.CENTER);

        //Faili valimis silt üleslaadimise kastis
        labelInput = new Label("Lohista fail siia või ava");
        labelInput.setId("dragboxlabel");

        //Faili valmis aken ja nupp (SIIT)
        FileChooser fileChooseInput = new FileChooser();
        fileChooseInput.setTitle("Ava fail");
        fileChooseInput.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Table files", "*.csv"));

        chooseFile = new Button("SIIT");
        chooseFile.setId("choosefilebutton");
        chooseFile.setOnAction(e -> {
            File selectedFile = fileChooseInput.showOpenDialog(window);
            if (selectedFile != null) {
                setFile(selectedFile);
            }
        });

        //Üles laaditud faili pilt
        dropFileImage = new Image("DropFile.png");
        dropFileView = new ImageView(dropFileImage);
        dropFileView.setLayoutY(10);
        dropFileView.setLayoutX((dragBox.getX() + dragBox.getWidth() / 2) - (dropFileView.getImage().getWidth() / 2));

        //Faili eemaldamise nupp
        closeButton = new Button();
        closeButton.setId("closebutton");
        closeButton.setLayoutY((dragBox.getY() + dragBox.getHeight() / 2) - (dropFileView.getImage().getHeight() / 2) - 28);
        closeButton.setLayoutX((dragBox.getX() + dragBox.getWidth() / 2) + (dropFileView.getImage().getWidth() / 2));
        closeButton.setDisable(true);
        closeButton.setVisible(false);
        closeButton.setOnAction(e -> removeFile());

        //Alustamise nupp
        startButton = new Button("Alusta");
        startButton.setOnAction(e -> {
            try {
                SorterCSV.startSorting(convertableFile); //Alustatakse sorteerimisega
                setProgressValue(1, 1);
                info.setText("Fail on loetud!");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        startButton.setId("startbutton");
        startButton.setDisable(true);
        startButton.setLayoutX(518);
        startButton.setPrefWidth(86);

        //Edenemise tahvel
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(515);
        progressBar.setLayoutY(8);

        //Info silt edenemise tahvli all
        info = new Label("Info: Käsi-XRF faili sorteerija");
        info.setLayoutY(20);


        //__________________LAYOUT_________________
        //Paneb kokku faili valimiseks sildi ja nupu SIIT üleslaadimise kastis
        HBox labelFile = new HBox(-8);
        labelFile.getChildren().addAll(labelInput, chooseFile);
        labelFile.setAlignment(Pos.CENTER);
        labelFile.setLayoutY(135);

        //Paneb kokku kõik, mis jääb üleslaadmise kasti
        fileInputPane = new Pane();
        fileInputPane.getChildren().addAll(dragBox, labelFile, dropFileView, fileName, closeButton);

        //Mõõdetakse kui lai kast on ning arvutatakse kasti keskkoht
        labelFile.layoutXProperty().bind(fileInputPane.widthProperty().subtract(labelFile.widthProperty()).divide(2));

        fileName.setLayoutY(130);
        fileName.layoutXProperty().bind(fileInputPane.widthProperty().subtract(fileName.widthProperty()).divide(2));

        //Pannakse kokku kõik, mis jääb üleslaadimiskasti alla
        Pane convertPane = new Pane();
        convertPane.getChildren().addAll(progressBar, startButton, info);

        //Pannakse kokku programmi info, üleslaadimise kast ja kõik muu
        Group fileInputGroup = new Group(fileInputPane);
        Group convertGroup = new Group(convertPane);

        VBox verticalPane = new VBox();
        verticalPane.getChildren().addAll(programInfo, fileInputGroup, convertGroup);
        verticalPane.setSpacing(20);

        Group verticalGroup = new Group(verticalPane);

        StackPane fileInputStack = new StackPane();
        fileInputStack.getChildren().addAll(verticalGroup);

        Scene scene = new Scene(fileInputStack);
        scene.getStylesheets().add("TU_THEME.css"); //Fail resources kausta all, kus on määratud stiilid nuppudele, väljadele jne...

        window.setScene(scene);
        window.setMinWidth(700);
        window.setMinHeight(400);
        window.show();

        //__DRAG_AND_DROP__
        fileInputPane.setOnDragOver(this::onDragOver);
        fileInputPane.setOnDragDropped(this::onDragDropped);
    }

    //Programm suletakse
    private void closeProgram() {
        window.close();
    }

    //Muudetakse edenemise tahvlit
    public static void setProgressValue(double value, double maxValue) {
        double result = (double) Math.round((value / maxValue) * 100) / 100;
        progressBar.setProgress(result);
    }

    //Vaadatakse kas alustamis nuppu saab kasutada
    private void checkStartConditions() {
        if (fileIsSet) {
            startButton.setDisable(false);
            info.setText("Info: Sorteerimisega saab alustada");
        } else {
            startButton.setDisable(true);
            info.setText("Info: Sorteeritav fail lisamata");
        }
    }

    //Muudetakse üleslaadimise kasti kui lisatakse fail
    private void setFile(File file) {
        convertableFile = file;
        dropFileView.setImage(new Image("csv.png"));
        fileName.setText(file.getName());
        labelInput.setVisible(false);
        chooseFile.setDisable(true);
        chooseFile.setVisible(false);
        closeButton.setDisable(false);
        closeButton.setVisible(true);
        fileIsSet = true;
        checkStartConditions();
    }

    //Eemaldatakse fail üleslaadimise kastist
    private void removeFile() {
        convertableFile = null;
        dropFileView.setImage(dropFileImage);
        fileName.setText("");
        labelInput.setVisible(true);
        chooseFile.setDisable(false);
        chooseFile.setVisible(true);
        closeButton.setDisable(true);
        closeButton.setVisible(false);
        fileIsSet = false;
        checkStartConditions();
    }

    //Mis juhtub kui tirida fail üleslaadmise kasti sisse ja hoida seda seal
    private void onDragOver(DragEvent dragEvent) {
        if (dragEvent.getGestureSource() != fileInputPane
                && dragEvent.getDragboard().hasFiles()) {

            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        dragEvent.consume();
    }

    //Mis juhtub kui fail üleslaadmise kasti sees lahti lasta
    private void onDragDropped(DragEvent dragEvent) {
        Dragboard db = dragEvent.getDragboard();
        boolean success = false;
        if (db.hasFiles() && db.getFiles().size() == 1) {
            File file = db.getFiles().get(0);
            Optional<String> fileExtension = Optional.of("csv");
            Optional<String> droppedFileExtension = getExtensionByStringHandling(file.getName());

            //Kui fail on csv lõpuga siis see lisatakse vastasel juhul antakse ette veaaken
            //Platform.runLater kasutatkse, et veaakna ette viskamisel ei jääks faili ikoon ekraanile ette seisma
            if (fileExtension.equals(droppedFileExtension)) {
                setFile(file);
                success = true;
            } else {
                Platform.runLater(() -> AlertBox.display("Viga!", "Lisatud fail peab olema *.csv lõpuga.", true));
            }

            //Kui faile on rohkem kui üks siis antakse ette veaaken,
        } else if (db.getFiles().size() > 1) {
            Platform.runLater(() -> AlertBox.display("Viga!", "Korraga saab lisada ainult 1 faili.", true));
        }

        dragEvent.setDropCompleted(success);
        dragEvent.consume();
    }

    //Sellega meetodiga saadakse faili laiend
    public Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public static void main(String[] args) {
        launch(args);
    }
}