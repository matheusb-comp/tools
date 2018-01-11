package com.lumenaut.poolmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @Author Luca Vignaroli
 * @Email luca@burning.it
 * @Date 10/01/2018 - 2:30 AM
 */
public class Main extends Application {
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //region FIELDS

    //endregion
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //region ACCESSORS

    //endregion
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //region CONSTRUCTORS

    /**
     * Constructor
     */
    public Main() {

    }

    //endregion
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //region METHOD OVERRIDES

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Build root
        final Parent root = FXMLLoader.load(getClass().getResource("/inflationManagerMain.fxml"));

        // Initialize the primary stage and show it
        primaryStage.setTitle("Inflation Pool Manager");
        primaryStage.setScene(new Scene(root, 1080, 750));
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/inflationManager.png")));
        primaryStage.show();
    }

    //endregion
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //region METHODS

    public static void main(String[] args) {
        launch(args);
    }

    //endregion
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
