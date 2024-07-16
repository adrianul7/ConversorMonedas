import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ConversorDeMonedasGUI extends JFrame {
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/efd9bdeae818ef935a3a28c6/latest/";
    private JTextField cantidadField;
    private JTextField monedaOrigenField;
    private JTextField monedaDestinoField;
    private JLabel resultadoLabel;

    public ConversorDeMonedasGUI() {
        setTitle("Conversor de Monedas");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        JLabel cantidadLabel = new JLabel("Cantidad:");
        cantidadField = new JTextField();
        JLabel monedaOrigenLabel = new JLabel("Moneda de Origen (ej. USD):");
        monedaOrigenField = new JTextField();
        JLabel monedaDestinoLabel = new JLabel("Moneda de Destino (ej. EUR):");
        monedaDestinoField = new JTextField();
        resultadoLabel = new JLabel("Resultado: ");

        JButton convertirButton = new JButton("Convertir");
        convertirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertirMoneda();
            }
        });

        add(cantidadLabel);
        add(cantidadField);
        add(monedaOrigenLabel);
        add(monedaOrigenField);
        add(monedaDestinoLabel);
        add(monedaDestinoField);
        add(convertirButton);
        add(new JLabel()); // Placeholder
        add(resultadoLabel);

        setVisible(true);
    }

    private void convertirMoneda() {
        try {
            double cantidad = Double.parseDouble(cantidadField.getText());
            String monedaOrigen = monedaOrigenField.getText().toUpperCase();
            String monedaDestino = monedaDestinoField.getText().toUpperCase();

            double tasaDeCambio = obtenerTasaDeCambio(monedaOrigen, monedaDestino);
            if (tasaDeCambio != -1) {
                double resultado = cantidad * tasaDeCambio;
                resultadoLabel.setText(String.format("%.2f %s son %.2f %s", cantidad, monedaOrigen, resultado, monedaDestino));
            } else {
                resultadoLabel.setText("No se pudo obtener la tasa de cambio.");
            }
        } catch (NumberFormatException e) {
            resultadoLabel.setText("Por favor, ingrese una cantidad válida.");
        }
    }

    private double obtenerTasaDeCambio(String monedaOrigen, String monedaDestino) {
        try {
            URL url = new URL(API_URL + monedaOrigen);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if (con.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(con.getInputStream());
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                reader.close();
                return jsonObject.getAsJsonObject("conversion_rates").get(monedaDestino).getAsDouble();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ConversorDeMonedasGUI();
            }
        });
    }
}