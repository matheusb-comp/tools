package com.lumenaut.poolmanager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.util.Properties;

/**
 * @Author Luca Vignaroli
 * @Email luca@burning.it
 * @Date 11/01/2018 - 6:05 PM
 */
public class Settings {
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //region FIELDS

    // Horizon networks
    public static final String HORIZON_TEST_NETWORK = "https://horizon-testnet.stellar.org";
    public static final String HORIZON_LIVE_NETWORK = "https://horizon.stellar.org";

    // Properties instance
    private static final Properties PROPERTIES = new Properties();

    // Default Settings
    public static String SETTING_OPERATIONS_NETWORK = "";
    public static String SETTING_INFLATION_POOL_ADDRESS = "";
    public static String SETTING_FEDERATION_NETWORK_INFLATION_URL = "";
    public static String SETTING_HORIZON_DB_ADDRESS = "";
    public static String SETTING_HORIZON_DB_PORT = "";
    public static String SETTING_HORIZON_DB_USER = "";
    public static String SETTING_HORIZON_DB_PASS = "";
    public static String SETTING_MEMO = "";
    public static long SETTING_FEE = 100;
    public static int SETTING_OPERATIONS_PER_TRANSACTION_BATCH = 100;
    public static String SETTING_DONATION_DATANAME_PREFIX = "";

    // Non persistent settings
    public static RoundingMode ROUNDING_MODE = RoundingMode.HALF_DOWN;


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
    private Settings() {

    }

    //endregion
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //region INTERFACES IMPLEMENTATIONS

    //endregion
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //region METHOD OVERRIDES

    //endregion
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //region METHODS

    /**
     * Load settings from the ini file in the working directory or set defaults
     */
    public static void loadSettings() throws IOException {
        try {
            // Attempt to load the file
            PROPERTIES.load(new FileInputStream("data/settings.ini"));

            // Bring the settings in the runtime
            SETTING_OPERATIONS_NETWORK = PROPERTIES.getProperty("operationsNetwork");
            SETTING_INFLATION_POOL_ADDRESS = XLMUtils.isPublicKeyValidFormat(PROPERTIES.getProperty("inflationPoolAddress")) ? PROPERTIES.getProperty("inflationPoolAddress") : "";
            SETTING_FEDERATION_NETWORK_INFLATION_URL = PROPERTIES.getProperty("fedNetworkInflationUrl");
            SETTING_MEMO = PROPERTIES.getProperty("memoText");
            SETTING_FEE = Long.parseLong(PROPERTIES.getProperty("fee")) < 100 ? 100 : Long.parseLong(PROPERTIES.getProperty("fee"));
            SETTING_DONATION_DATANAME_PREFIX = PROPERTIES.getProperty("donationsPrefix");

            SETTING_HORIZON_DB_ADDRESS = PROPERTIES.getProperty("horizonDbAddress");
            SETTING_HORIZON_DB_PORT = PROPERTIES.getProperty("horizonDbPort");
            SETTING_HORIZON_DB_USER = PROPERTIES.getProperty("horizonDbUser");
            SETTING_HORIZON_DB_PASS = PROPERTIES.getProperty("horizonDbPass");
        } catch (Exception ignore) {
            // Init defaults
            SETTING_OPERATIONS_NETWORK = PROPERTIES.getProperty("operationsNetwork", "TEST");
            SETTING_INFLATION_POOL_ADDRESS = PROPERTIES.getProperty("inflationPoolAddress", "");
            SETTING_FEDERATION_NETWORK_INFLATION_URL = PROPERTIES.getProperty("fedNetworkInflationUrl", "https://fed.network/inflation/");
            SETTING_MEMO = PROPERTIES.getProperty("memoText", "Thanks from lumenaut.net");
            SETTING_DONATION_DATANAME_PREFIX = PROPERTIES.getProperty("donationsPrefix", "lumenaut.net donation");

            // Try to parse the fee, or just default to 100
            try {
                SETTING_FEE = Long.parseLong(PROPERTIES.getProperty("fee", "100"));
            } catch (NumberFormatException e) {
                SETTING_FEE = 100;
            }

            SETTING_HORIZON_DB_ADDRESS = PROPERTIES.getProperty("horizonDbAddress", "");
            SETTING_HORIZON_DB_PORT = PROPERTIES.getProperty("horizonDbPort", "");
            SETTING_HORIZON_DB_USER = PROPERTIES.getProperty("horizonDbUser", "");
            SETTING_HORIZON_DB_PASS = PROPERTIES.getProperty("horizonDbPass", "");

            // Save defaults
            saveSettings();
        }
    }

    /**
     * Save settings to the ini file in the working directory
     *
     * @throws IOException
     */
    public static void saveSettings() throws IOException {
        // Get current settings
        PROPERTIES.setProperty("operationsNetwork", SETTING_OPERATIONS_NETWORK);
        PROPERTIES.setProperty("inflationPoolAddress", XLMUtils.isPublicKeyValidFormat(SETTING_INFLATION_POOL_ADDRESS) ? SETTING_INFLATION_POOL_ADDRESS : "");
        PROPERTIES.setProperty("fedNetworkInflationUrl", SETTING_FEDERATION_NETWORK_INFLATION_URL);
        PROPERTIES.setProperty("memoText", SETTING_MEMO);
        PROPERTIES.setProperty("fee", SETTING_FEE >= 100 ? String.valueOf(SETTING_FEE) : "100");
        PROPERTIES.setProperty("donationsPrefix", SETTING_DONATION_DATANAME_PREFIX);

        PROPERTIES.setProperty("horizonDbAddress", SETTING_HORIZON_DB_ADDRESS);
        PROPERTIES.setProperty("horizonDbPort", SETTING_HORIZON_DB_PORT);
        PROPERTIES.setProperty("horizonDbUser", SETTING_HORIZON_DB_USER);
        PROPERTIES.setProperty("horizonDbPass", SETTING_HORIZON_DB_PASS);

        // Store
        PROPERTIES.store(new FileOutputStream("data/settings.ini"), "Settings");
    }

    //endregion
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

