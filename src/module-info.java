module sistema {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
	requires javafx.base;

    // Pacotes abertos para reflexão do JavaFX
    opens application to javafx.fxml;
    opens application.view to javafx.fxml;   // 🔥 necessário para injeção de campos @FXML
    opens application.model to javafx.base;
    opens application.dao to javafx.base;

    // Pacotes exportados
    exports application;
    exports application.view;
    exports application.model;
    exports application.dao;
}
