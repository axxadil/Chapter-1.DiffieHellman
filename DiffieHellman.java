package diffiehellman;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigInteger;

public class DiffieHellman extends JFrame {
    private JTextField aField, bField, pField, xField, yField, aliceKeyField, bobKeyField;
    private JButton calculateButton;
    private JLabel alicePublicKeyLabel, bobPublicKeyLabel, sharedSecretLabel;

    public DiffieHellman() {
        super("Diffie-Hellman Key Exchange");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 300);
        setLayout(new GridLayout(0, 2));

        aField = new JTextField();
        bField = new JTextField();
        pField = new JTextField();
        xField = new JTextField();
        yField = new JTextField();
        aliceKeyField = new JTextField();
        bobKeyField = new JTextField();
        calculateButton = new JButton("Вычислить");

        alicePublicKeyLabel = new JLabel("Публичный ключ Алисы: ");
        bobPublicKeyLabel = new JLabel("Публичный ключ Боба: ");
        sharedSecretLabel = new JLabel("Общий секретный ключ: ");

        add(new JLabel("a:"));
        add(aField);
        add(new JLabel("b:"));
        add(bField);
        add(new JLabel("mod p:"));
        add(pField);
        add(new JLabel("x:"));
        add(xField);
        add(new JLabel("y:"));
        add(yField);
        add(new JLabel("Приватный ключ Алисы:"));
        add(aliceKeyField);
        add(new JLabel("Приватный ключ Боба:"));
        add(bobKeyField);
        add(calculateButton);
        add(alicePublicKeyLabel);
        add(bobPublicKeyLabel);
        add(sharedSecretLabel);

        calculateButton.addActionListener(this::performCalculation);
        setVisible(true);
    }

    private void performCalculation(ActionEvent e) {
        BigInteger a = new BigInteger(aField.getText());
        BigInteger b = new BigInteger(bField.getText());
        BigInteger p = new BigInteger(pField.getText());
        BigInteger x = new BigInteger(xField.getText());
        BigInteger y = new BigInteger(yField.getText());
        BigInteger aliceKey = new BigInteger(aliceKeyField.getText());
        BigInteger bobKey = new BigInteger(bobKeyField.getText());

        EllipticCurvePoint basePoint = new EllipticCurvePoint(x, y, a, b, p);
        EllipticCurvePoint alicePublic = basePoint.multiply(aliceKey);
        EllipticCurvePoint bobPublic = basePoint.multiply(bobKey);

        EllipticCurvePoint sharedSecret = alicePublic.multiply(bobKey);

        alicePublicKeyLabel.setText("Публичный ключ Алисы: " + alicePublic);
        bobPublicKeyLabel.setText("Публичный ключ Боба: " + bobPublic);
        sharedSecretLabel.setText("Общий секретный ключ: " + sharedSecret);
    }

    public class EllipticCurvePoint {
        private BigInteger x, y, a, b, p;

        public EllipticCurvePoint(BigInteger x, BigInteger y, BigInteger a, BigInteger b, BigInteger p) {
            this.x = x;
            this.y = y;
            this.a = a;
            this.b = b;
            this.p = p;
        }

        public EllipticCurvePoint add(EllipticCurvePoint o) {
            if (this.x.equals(o.x) && this.y.equals(o.y)) {
                return doublePoint();
            }
            BigInteger lambda = o.y.subtract(this.y).multiply(o.x.subtract(this.x).modInverse(p)).mod(p);
            BigInteger xr = lambda.multiply(lambda).subtract(this.x).subtract(o.x).mod(p);
            BigInteger yr = lambda.multiply(this.x.subtract(xr)).subtract(this.y).mod(p);
            return new EllipticCurvePoint(xr, yr, a, b, p);
        }

        public EllipticCurvePoint doublePoint() {
            BigInteger lambda = this.x.pow(2).multiply(new BigInteger("3")).add(a).multiply(this.y.multiply(new BigInteger("2")).modInverse(p)).mod(p);
            BigInteger xr = lambda.multiply(lambda).subtract(this.x.multiply(new BigInteger("2"))).mod(p);
            BigInteger yr = lambda.multiply(this.x.subtract(xr)).subtract(this.y).mod(p);
            return new EllipticCurvePoint(xr, yr, a, b, p);
        }

        public EllipticCurvePoint multiply(BigInteger k) {
            EllipticCurvePoint result = null;
            EllipticCurvePoint temp = this;
            while (k.signum() != 0) {
                if (k.testBit(0)) {
                    result = (result == null) ? temp : result.add(temp);
                }
                temp = temp.doublePoint();
                k = k.shiftRight(1);
            }
            return result;
        }

        @Override
        public String toString() {
            return x == null || y == null ? "Infinity" : "(" + x + ", " + y + ")";
        }
    }

    public static void main(String[] args) {
        new DiffieHellman();
    }

}
