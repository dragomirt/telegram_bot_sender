package main.java.dragomirturcanu;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Interfata extends Application {
    private TelegramTransmitatorNotificatii ttn_instance = null;
    private ArrayList<HashMap> chats = null;

    public static void run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.initializare(primaryStage);
    }

    private void initializare(Stage stage) {
        stage.setTitle("Dragomir Telegram Bot Manager");
        this.ttn_instance = new TelegramTransmitatorNotificatii();

        Label descriptionLabel = new Label();
        descriptionLabel.setText("Panelul de administrare a Telegram Bot-ului pentru raspandirea informatiei catre utilizatori.");

        Label lblToken = new Label("Token:");
        TextField fieldToken = new TextField();
        Button getDataButton = new Button("Incarca Conversatiile");

        VBox tokenVbox = new VBox();
        tokenVbox.setSpacing(10);
        tokenVbox.getChildren().addAll(lblToken, fieldToken, getDataButton);

        ListView chatList = new ListView();
        chatList.setMinHeight(250);

        Label lblCHAT_ID = new Label("Chats:");
//        TextField fieldCHAT_ID = new TextField();

        HBox keysHbox = new HBox();
        keysHbox.setAlignment(Pos.BASELINE_CENTER);
        keysHbox.setSpacing(8);
        keysHbox.getChildren().addAll(tokenVbox, lblCHAT_ID, chatList);

        GridPane root = new GridPane();
        root.setHgap(8);
        root.setVgap(8);
        root.setPadding(new Insets(5));

        ColumnConstraints cons1 = new ColumnConstraints();
        cons1.setHgrow(Priority.NEVER);
        root.getColumnConstraints().add(cons1);

        ColumnConstraints cons2 = new ColumnConstraints();
        cons2.setHgrow(Priority.ALWAYS);

        root.getColumnConstraints().addAll(cons1, cons2);

        RowConstraints rcons1 = new RowConstraints();
        rcons1.setVgrow(Priority.NEVER);

        RowConstraints rcons2 = new RowConstraints();
        rcons2.setVgrow(Priority.ALWAYS);

        root.getRowConstraints().addAll(rcons1, rcons2);

        Separator separator = new Separator();

        Label lbl = new Label("Titlu:");
        TextField field = new TextField();

        Label lbl2 = new Label("Continut:");
        TextField contentfield = new TextField();
        contentfield.setMinHeight(200);

        Button okBtn = new Button("Transmite");
        Button closeBtn = new Button("Stinge");
        HBox buttonBox = new HBox();

        closeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Platform.exit();
            }
        });

        buttonBox.setAlignment(Pos.BASELINE_RIGHT);
        buttonBox.setSpacing(8);
        buttonBox.getChildren().addAll(okBtn, closeBtn);

        GridPane.setHalignment(okBtn, HPos.RIGHT);

        root.add(descriptionLabel, 0,0, 4, 1);
        root.add(keysHbox, 0,1, 4, 1);
        root.add(separator, 0,2, 4, 1);

        root.add(lbl, 0, 3);
        root.add(field, 1, 3, 3, 1);

        root.add(lbl2, 0, 4, 4, 1);
        root.add(contentfield, 0, 5, 4, 1);

        root.add(buttonBox, 0, 6, 4, 1);

        stage.setScene(new Scene(root, 800, 1000));
        stage.show();

        Interfata local = this;

        getDataButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                local.chats = null;
                chatList.getItems().clear();

                local.ttn_instance.setTOKEN(fieldToken.getText());
                try {
                    local.chats = local.ttn_instance.getChats();
                    if (local.chats == null) {
                        return;
                    }

                    for (int i = 0; i < local.chats.size(); i++) {
                        HashMap chatItem = local.chats.get(i);
//                        chatList.getItems().add(chatItem.get("chat_id") + " | " + chatItem.get("first_name") + " ( " + chatItem.get("username") + " )");
                        chatList.getItems().add(chatItem.get("chat_id") + " | " + chatItem.get("first_name"));
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        okBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                TelegramTransmitatorNotificatii ttn = new TelegramTransmitatorNotificatii();
                ObservableList selectedItems = chatList.getSelectionModel().getSelectedItems();
                Object[] items = selectedItems.toArray();

                for (int i = 0; i < items.length; i++) {
                    String item = items[i].toString();
                    String chat_id = item.split(" | ")[0].toString();

                    ttn.setCHAT_ID(chat_id);
                    ttn.setTOKEN(fieldToken.getText());

                    try {
                        ttn.transmite(field.getText(), contentfield.getText());
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        });
    }
}
