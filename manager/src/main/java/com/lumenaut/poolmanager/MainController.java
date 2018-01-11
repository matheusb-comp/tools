package com.lumenaut.poolmanager;

import com.lumenaut.poolmanager.InflationDataFormat.InflationDataEntry;
import com.lumenaut.poolmanager.InflationDataFormat.InflationDataRoot;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.lumenaut.poolmanager.InflationDataFormat.OBJECT_MAPPER;
import static com.lumenaut.poolmanager.Settings.SETTING_INFLATION_POOL_ADDRESS;

/**
 * @Author Luca Vignaroli
 * @Email luca@burning.it
 * @Date 10/01/2018 - 4:38 PM
 */
public class MainController {
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //region FXML BINDINGS

    ////////////////////////////////////////////////////////
    // UI
    @FXML
    private AnchorPane primaryStage;

    @FXML
    private ProgressBar progressBar;

    ////////////////////////////////////////////////////////
    // BUTTONS
    @FXML
    private MenuItem settingsBtn;

    @FXML
    private MenuItem closeBtn;

    @FXML
    private Button getFederationDataBtn;

    @FXML
    private Button payBtn;

    ////////////////////////////////////////////////////////
    // TEXT FIELDS
    @FXML
    private TextField poolAddressTextField;

    @FXML
    private TextField poolSecretTextField;

    ////////////////////////////////////////////////////////
    // TEXT AREAS
    @FXML
    private TextArea inflationPoolDataTextArea;

    ////////////////////////////////////////////////////////
    // LABELS
    @FXML
    private Label poolDataVotersLabel;

    @FXML
    private Label poolDataTotalVotesLabel;


    //endregion
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //region FIELDS

    // Formatter
    private final DecimalFormat xlmFormatter = new DecimalFormat("#,###,###,###,##0.00");

    // Busy signal
    private final AtomicBoolean applicationBusy = new AtomicBoolean(false);

    // Buttons to disable when the application is busy
    private final List<Button> statefulButtons = new ArrayList<>();

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
    public MainController() {

    }

    //endregion
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //region INTERFACES IMPLEMENTATIONS

    //endregion
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //region METHOD OVERRIDES

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Load application settings
        try {
            Settings.loadSettings();
        } catch (IOException e) {
            showError("Error initializing application settings: " + e.getMessage());
        }

        // Set the default pool address if we have it in the settings
        if (SETTING_INFLATION_POOL_ADDRESS != null && !SETTING_INFLATION_POOL_ADDRESS.isEmpty()) {
            poolAddressTextField.setText(SETTING_INFLATION_POOL_ADDRESS);
        }

        // Add all buttons that should react to the application "busy" state
        statefulButtons.add(getFederationDataBtn);
        statefulButtons.add(payBtn);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // MENU BUTTONS HANDLERS

        // Close
        closeBtn.setOnAction(event -> Platform.exit());

        // Settings
        settingsBtn.setOnAction(event -> openSettingsWindow());

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // GET FEDERATION DATA BUTTON HANDLER

        getFederationDataBtn.setOnAction(event -> {
            // Get the target pool key
            final String poolAddress = poolAddressTextField.getText();

            // Check if we have an address
            // TODO Also check if it's a valid hash or let the request fail?
            if (poolAddress == null || poolAddress.isEmpty()) {
                showError("You must specify the inflation pool's address below");
            } else {
                // Clear existing data
                inflationPoolDataTextArea.clear();
                resetPoolCounters();

                // Start spinning
                setBusyState(true);

                // Build and submit async task
                final CompletableFuture<String> request = CompletableFuture.supplyAsync(() -> {
                    try {
                        return FederationNetwork.getVoters(poolAddress);
                    } catch (Exception e) {
                        // Cancel applicationBusy state and show error
                        Platform.runLater(() -> {
                            setBusyState(false);
                            showError(e.getMessage());
                        });
                    }

                    return null;
                });

                // Process task completion
                request.thenAccept(inflationPoolData -> {
                    // Update text area
                    inflationPoolDataTextArea.setText(inflationPoolData);

                    // Cancel applicationBusy state
                    Platform.runLater(() -> {
                        setBusyState(false);
                        refreshPoolCounters();
                    });
                });
            }
        });
    }

    //endregion
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //region METHODS

    /**
     * Shows an error dialog
     *
     * @param message
     */
    private void showError(final String message) {
        final Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
        alert.show();
    }

    /**
     * Shows an information dialog
     *
     * @param message
     */
    private void showInfo(final String message) {
        final Alert alert = new Alert(AlertType.INFORMATION, message, ButtonType.OK);
        alert.show();
    }

    /**
     * Updates the pool data counters
     */
    private void refreshPoolCounters() {
        final String inflationPoolData = inflationPoolDataTextArea.getText();
        try {
            final InflationDataRoot inflationDataRoot = OBJECT_MAPPER.readValue(inflationPoolData, InflationDataRoot.class);
            poolDataVotersLabel.setText(String.valueOf(inflationDataRoot.getEntries().size()));

            Long totalVotes = 0L;
            for (InflationDataEntry voter : inflationDataRoot.getEntries()) {
                totalVotes += voter.getBalance();
            }

            poolDataTotalVotesLabel.setText(xlmFormatter.format(totalVotes / 10000000L) + " XLM");
        } catch (IOException e) {
            showError("Cannot compute pool data: " + e.getMessage());
        }
    }

    /**
     * Resets the pool data counters
     */
    private void resetPoolCounters() {
        poolDataVotersLabel.setText("0");
        poolDataTotalVotesLabel.setText("0 XLM");
    }

    /**
     * Set the application's busy state. While busy the application will set the progress bar to an infinite spin
     * and disable all stateful buttons. When exiting the busy state everything is restored.
     *
     * @param newState
     */
    private void setBusyState(boolean newState) {
        if (applicationBusy.get() != newState) {
            applicationBusy.set(newState);
            progressBar.setProgress((newState ? -1 : 0));

            // Enable or disable all statefulButtons added to the list based on the new applicationBusy state
            for (Button button : statefulButtons) {
                button.setDisable(newState);
            }
        }
    }

    /**
     * Open the settings panel
     */
    private void openSettingsWindow() {
        // Build root
        try {
            // Create new stage
            final Stage settingsStage = new Stage();
            final Parent settingsScene = FXMLLoader.load(getClass().getResource("/inflationManagerSettings.fxml"));

            // Initialize the settings stage and show it
            settingsStage.setTitle("Settings");
            settingsStage.setScene(new Scene(settingsScene, 600, 400));
            settingsStage.getIcons().add(new Image(Main.class.getResourceAsStream("/inflationManager.png")));
            settingsStage.initModality(Modality.WINDOW_MODAL);
            settingsStage.initOwner(primaryStage.getScene().getWindow());
            settingsStage.show();
        } catch (IOException e) {
            showError(e.getMessage());
        }
    }

    //endregion
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
