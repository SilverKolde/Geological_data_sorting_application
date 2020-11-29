import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class AlertBox {

    //______MEETODID______
    public static void display(String title, String message, boolean modality) {
        Stage window = new Stage();
        window.getIcons().add(new Image("Icon.png"));
        //Kas saab väljaspool akent midagi klikkida kuni aken avatud
        if (modality)
            window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);
        label.setStyle("-fx-text-fill: #FC4A3C;");

        Button closeButton = new Button("Ok");
        closeButton.setOnAction(e -> window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10, 20, 10, 20));

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("TU_THEME.css");
        window.setScene(scene);
        window.showAndWait();
    }

}