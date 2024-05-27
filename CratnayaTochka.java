package diffiehellman;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;

public class CratnayaTochka extends JFrame {
    private JTextField aField, bField, pField, xField, yField, kField;
    private JButton calculateButton;
    private JLabel resultLabel;

    public CratnayaTochka() {
        setTitle("Elliptic Curve Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(400, 300);

        aField = new JTextField(10);
        bField = new JTextField(10);
        pField = new JTextField(10);
        xField = new JTextField(10);
        yField = new JTextField(10);
        kField = new JTextField(10);
        calculateButton = new JButton("Вычислить");
        resultLabel = new JLabel("Ответ:");

        add(new JLabel("a:"));
        add(aField);
        add(new JLabel("b:"));
        add(bField);
        add(new JLabel("Mod p:"));
        add(pField);
        add(new JLabel("X coordinate of P:"));
        add(xField);
        add(new JLabel("Y coordinate of P:"));
        add(yField);
        add(new JLabel("k:"));
        add(kField);
        add(calculateButton);
        add(resultLabel);

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateResult();
            }
        });

        setVisible(true);
    }

    private void calculateResult() {
        try {
            BigInteger a = new BigInteger(aField.getText());
            BigInteger b = new BigInteger(bField.getText());
            BigInteger p = new BigInteger(pField.getText());
            BigInteger x = new BigInteger(xField.getText());
            BigInteger y = new BigInteger(yField.getText());
            BigInteger k = new BigInteger(kField.getText());

            EllipticCurvePoint P = new EllipticCurvePoint(x, y, a, b, p);
            EllipticCurvePoint result = P.multiply(k);
            resultLabel.setText("Result: " + result);
        } catch (Exception ex) {
            resultLabel.setText("Error: " + ex.getMessage());
        }
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
        new CratnayaTochka();
    }
}
